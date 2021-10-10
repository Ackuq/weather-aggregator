package id2221.consumers

import org.slf4j.LoggerFactory;
import org.apache.kafka.clients.consumer.{KafkaConsumer, ConsumerConfig}

import java.util.{Properties, Arrays}

import scala.jdk.DurationConverters._;
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global;
import scala.concurrent.duration.DurationInt;

object ResultConsumer {
  final val logger = LoggerFactory.getLogger(this.getClass().getName());
  final val TOPIC = "result";
  final val BROKERS =
    scala.util.Properties.envOrElse("BROKERS", "localhost:9092");

  private def constructConsumer() = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKERS)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "http-proxy")
    props.put(
      ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringDeserializer"
    )
    props.put(
      ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringDeserializer"
    )
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")

    val consumer = new KafkaConsumer[String, String](props)
    consumer.subscribe(Arrays.asList("request"));
    consumer
  }

  def awaitValue(uuid: String): Future[String] = Future {
    val consumer = constructConsumer()
    try {
      logger.info("Starting")
      consumer.subscribe(Arrays.asList(TOPIC));
      while (true) {
        val records = consumer.poll(200.milli.toJava)
        records.forEach(action => {
          val key = action.key();
          if (key.equals(uuid)) {
            // TODO: Parse the value
            val value = action.value();
            return Future.successful(value);
          }
        })
      }
      throw new Exception("Did not receive any result")
    } catch {
      case e: Throwable => {
        throw e
      }
    } finally {
      consumer.close()
    }
  }
}
