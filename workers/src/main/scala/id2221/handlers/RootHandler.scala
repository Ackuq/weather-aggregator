package id2221.handlers

import java.util.Properties
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.{Arrays, Properties}
import java.time.ZonedDateTime
import org.slf4j.LoggerFactory;

abstract class RootHandler extends Runnable {
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

  // Must be overriden
  def handleMessage(
      uuid: String,
      longitude: Double,
      latitude: Double,
      dateOption: Option[ZonedDateTime]
  )

  def run(): Unit = {
    logger.info("Starting consumer loop...")
    while (true) {
      val records = consumer.poll(1000)
      records.forEach(data => {
        try {
          val uuid = data.key();
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
          case _: Throwable => println("Failed to parse record")
        }
      });
    }
  }
}
