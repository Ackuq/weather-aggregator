package id2221.consumers

import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.{Properties, Arrays}

import scala.jdk.DurationConverters._;
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global;
import scala.concurrent.duration.DurationInt;

object ResultConsumer {
  final val logger = LoggerFactory.getLogger(this.getClass().getName());
  final val BROKERS =
    scala.util.Properties.envOrElse("BROKERS", "http://kafka:9092");

  private def constructConsumer() = {
    val props = new Properties()
    props.put("bootstrap.servers", BROKERS)
    props.put(
      "key.deserializer",
      "org.apache.kafka.common.serialization.StringDeserializer"
    )
    props.put(
      "value.deserializer",
      "org.apache.kafka.common.serialization.StringDeserializer"
    )
    props.put("auto.offset.reset", "latest")
    props.put("group.id", "workers")
    val consumer = new KafkaConsumer[String, String](props)
    consumer.subscribe(Arrays.asList("request"));
    consumer
  }

  val consumer = constructConsumer();

  def awaitValue(uuid: String): Future[String] = Future {
    while (true) {
      val records = consumer.poll(200.milli.toJava)
      records.forEach(action => {
        val key = action.key();
        if (key.equals(uuid)) {
          // TODO: Parse the value
          val value = action.value();
          return Future.successful(value)
        }
      })
    }
    throw new Exception("Did not receive any result")
  }
}
