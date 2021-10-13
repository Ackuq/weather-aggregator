package id2221

import java.util.Properties
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.KafkaProducer
import id2221.common.Forecast
import org.apache.kafka.clients.producer.ProducerRecord
import org.slf4j.LoggerFactory



class SparkProducer(createProducer: () => KafkaProducer[String, String]) extends Serializable{
  final val TOPIC = "result";  
  // Delay instantiating the producer to avoid producer not being serializable when sending it from the driver node to executor nodes
  lazy val producer = createProducer()
  
  def sendTemp(avgTemp: Double, uuid: String) = {
    println(s"Sending avg temperature $avgTemp to $uuid");
    val data =
      new ProducerRecord[String, String](TOPIC, uuid, avgTemp.toString());
      producer.send(data); 
    }
  }
  
  object SparkProducer {
    
    // Kafka config values
    final val BROKERS = scala.util.Properties.envOrElse("BROKERS", "kafka:9092");

    val kafkaConfig = {
      val conf = new Properties();
      conf.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKERS);
      conf.put(ProducerConfig.CLIENT_ID_CONFIG, "spark-producer");
      conf.put(
        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringSerializer"
      );
      conf.put(
        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
        "org.apache.kafka.common.serialization.StringSerializer"
      );
      conf
    }

   def apply(): SparkProducer = {
     val f = () => {
      val producer = new KafkaProducer[String, String](kafkaConfig);

      // Send all buffered producer messages when closing the JVM
      sys.addShutdownHook {
        producer.close()
      }
      producer
     }
    new SparkProducer(f);
  }
}