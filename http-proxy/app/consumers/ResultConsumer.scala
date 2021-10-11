package id2221.consumers

import play.api.Logger;
import org.apache.kafka.clients.consumer.{KafkaConsumer, ConsumerConfig}

import java.util.{Properties, Arrays}

import scala.jdk.DurationConverters._;
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global;
import scala.concurrent.duration.DurationInt;
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import scala.util.Success
import scala.util.Failure

object ResultConsumer {
  final val logger = Logger(this.getClass().getName());
  final val TOPIC = "result";
  final val BROKERS =
    scala.util.Properties.envOrElse("BROKERS", "localhost:9092");

  private def constructConsumer(uuid: String) = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKERS);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, s"http-proxy-$uuid")
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

    new KafkaConsumer[String, String](props)
  }

  private def insideConsumerLoop(
      uuid: String,
      consumer: KafkaConsumer[String, String]
  ): String = {
    while (true) {
      val records = consumer.poll(200.milli.toJava)
      records.forEach(action => {
        val key = action.key();
        if (key.equals(uuid)) {
          // TODO: Parse the value
          logger.info(s"Got results, closing consumer for uuid $uuid")
          val value = action.value();
          return value;
        }
      })
    }
    throw new Exception("Did not receive any result")
  }

  def awaitValue(uuid: String): Future[String] = Future {
    logger.info("Starting consumer...")
    val consumer = constructConsumer(uuid)
    consumer.subscribe(Arrays.asList(TOPIC));
    try {
      insideConsumerLoop(uuid, consumer)
    } finally {
      logger.info("Stopping consumer...")
      consumer.close()
    }
  }
}
