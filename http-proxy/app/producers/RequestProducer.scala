package id2221.producers;

import play.api.Logger;
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig}
import java.util.Properties
import id2221.common.Payload
import org.apache.kafka.clients.producer.ProducerRecord

object RequestProducer {
  final val logger = Logger(this.getClass().getName())
  final val TOPIC = "request";
  final val BROKERS =
    scala.util.Properties.envOrElse("BROKERS", "localhost:9092");

  private def constructProducer() = {
    logger.info("Starting producer...")
    val props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKERS);
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "RequestProducer");
    props.put(
      ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer"
    );
    props.put(
      ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
      "org.apache.kafka.common.serialization.StringSerializer"
    );
    new KafkaProducer[String, String](props);
  }

  val producer = constructProducer();

  def requestData(payload: Payload, uuid: String) = {
    logger.info("Sending forecast to Kafka...")
    // The value will be a stringified version of the forecast, need to be matched to serialize
    val data =
      new ProducerRecord[String, String](TOPIC, uuid, payload.toString());
    producer.send(data);
  }

}
