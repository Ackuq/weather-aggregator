package id2221.spark;

import java.util.Properties
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.spark.SparkConf
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.commons.codec.StringDecoder
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.kafka.common.serialization.StringDeserializer
import id2221.common.ForecastParser
import org.apache.spark.streaming.State
import id2221.common.Forecast
import org.apache.log4j.Level
import org.apache.log4j.Logger
import id2221.SparkProducer


class SparkConsumer extends Runnable {

  final val TOPIC = "forecast";
  final val BROKERS = scala.util.Properties.envOrElse("BROKERS", "kafka:9092");
  def run(): Unit = {

    Logger.getLogger("org.apache.spark").setLevel(Level.WARN);

    // Create Spark kafka input stream
    val conf = new SparkConf().setAppName("SparkApplication").setMaster("local[1]")
    val ssc = new StreamingContext(conf, Seconds(1));
    ssc.checkpoint("./checkpoints/");
    val kafkaConf = Map(
      "bootstrap.servers" -> BROKERS,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "spark-consumer",
      "enable.auto.commit" -> (false: java.lang.Boolean)
      );
    val topics = Set(TOPIC);
    val inputStream = KafkaUtils.createDirectStream[String, String](ssc, PreferConsistent, Subscribe[String, String](topics, kafkaConf));

    // Creeate spark producer using sparkcontext.broadcast, to make sure it is only sent once to the executor nodes
    val producer = ssc.sparkContext.broadcast(SparkProducer());
    // To save incoming weatherclients until forecasts are recieved by all workers
    val weatherClients = scala.collection.mutable.Map[String, List[Option[Forecast]]]();

    inputStream.foreachRDD { rdd => 
      rdd.foreach { content => 
        val clientId: String = content.key();
        val forecast: Option[Forecast] = ForecastParser.toForecast(content.value());
        

        forecast match {
          case Some(f) => 
            println(s"Received ${f.provider}-forecast from UUID $clientId")
          case None => 
            println("Received failed forecast");
        }

        if(weatherClients.contains(clientId)){
          weatherClients(clientId) = forecast :: weatherClients(clientId);
        }
        else {
          weatherClients.put(clientId, List(forecast))
        }

        println(s"Current list size for $clientId is ${weatherClients(clientId).size}");

        // If three or more forecasts are received, we have received from all workers and can calculate average and send back to client
        if(weatherClients(clientId).size >= 3) {
          println(s"Received all forecasts for client ${clientId}, sending response back to client.")
          val tempAvg = weatherClients(clientId).flatten.map(f => f.temperature).reduce((a ,b) => a + b)/weatherClients(clientId).size;
          weatherClients.-(clientId);
          producer.value.sendTemp(tempAvg, clientId);
        }
      }
    }

    ssc.start()
    ssc.awaitTermination()
  }
}