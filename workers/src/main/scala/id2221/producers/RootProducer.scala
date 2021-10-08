package id2221.producers

import java.util.Properties;
import org.apache.kafka.clients.producer.{
  ProducerConfig,
  KafkaProducer,
  ProducerRecord
}
import org.slf4j.LoggerFactory;
import id2221.common.Forecast

abstract class RootProducer(clientId: String) {
  final val logger = LoggerFactory.getLogger(this.getClass().getName())
  final val TOPIC = "forecast";
  final val BROKERS =
    scala.util.Properties.envOrElse("BROKERS", "http://kafka:9092");
  final val CLIENT_ID = clientId;

  private def constructProducer() = {
    logger.info("Starting producer...")
    val props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BROKERS);
    props.put(ProducerConfig.CLIENT_ID_CONFIG, CLIENT_ID);
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

  val producer = constructProducer()

  def sendForecast(forecast: Forecast, uuid: String) = {
    logger.info("Sending forecast to Kafka...")
    // The value will be a stringified version of the forecast, need to be matched to serialize
    val data =
      new ProducerRecord[String, String](forecast, uuid, forecast.toString());
    producer.send(data);
  }
}
