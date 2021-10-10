package id2221.handlers

import java.util.Properties
import org.apache.kafka.clients.consumer.{KafkaConsumer, ConsumerConfig}
import java.util.{Collections, Properties}
import java.time.ZonedDateTime
import org.slf4j.LoggerFactory;

abstract class RootHandler extends Runnable {
  final val logger = LoggerFactory.getLogger(this.getClass().getName());
  final val TOPIC = "request";
  final val BROKERS =
    scala.util.Properties.envOrElse("BROKERS", "localhost:9092");

  private def constructConsumer() = {
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKERS)
    props.put(ConsumerConfig.GROUP_ID_CONFIG, s"workers-${this.getClass()}")
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

  // Must be overriden
  def handleMessage(
      uuid: String,
      longitude: Double,
      latitude: Double,
      dateOption: Option[ZonedDateTime]
  )

  def run(): Unit = {
    val consumer = constructConsumer();
    logger.info("Starting consumer loop...")
    try {
      consumer.subscribe(Collections.singletonList(TOPIC))
      while (true) {
        val records = consumer.poll(1000)
        records.forEach(data => {
          try {
            val uuid = data.key();
            logger.info(s"Got request with uuid $uuid");
            val payload = data.value();
            payload match {
              case s"Payload($longitude,$latitude,$date)" => {
                val dateOption: Option[ZonedDateTime] = date match {
                  case "None"  => None
                  case dateStr => Some(ZonedDateTime.parse(dateStr))
                }
                handleMessage(
                  uuid,
                  longitude.toDouble,
                  latitude.toDouble,
                  dateOption
                )
              }
            }
          } catch {
            case e: Throwable => {
              logger.info("Failed to parse record");
              e.printStackTrace();
            }
          }
        });
      }
    } catch {
      case e: Throwable => {
        logger.info("Consumer loop stopped")
        e.printStackTrace();
      }
    } finally {
      consumer.close();
    }
  }
}
