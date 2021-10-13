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
import play.api.cache.SyncCacheApi

class ResultConsumer(cache: SyncCacheApi) extends Runnable {
  final val logger = Logger(this.getClass().getName());
  final val TOPIC = "result";
  final val BROKERS =
    scala.util.Properties.envOrElse("BROKERS", "localhost:9092");

  val props = new Properties()
  props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKERS);
  props.put(ConsumerConfig.GROUP_ID_CONFIG, s"http-proxy")
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
  consumer.subscribe(Arrays.asList(TOPIC));

  def run() = {
    logger.info("Starting consumer loop...")
    while (true) {
      val records = consumer.poll(200.milli.toJava)
      records.forEach(action => {
        try {
          val key = action.key();
          logger.info(s"Setting result value for uuid $key")
          cache.set(key, action.value(), 20.seconds);
        } catch {
          case exception: Throwable => {
            logger.warn(s"Error handling message $action")
          }
        }
      })
    }
  }
}
