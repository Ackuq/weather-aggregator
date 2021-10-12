package id2221.tests

import java.util.{Date, Properties}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, ProducerConfig}
import java.util.UUID;


class ProducerTest extends Runnable {
  def run() : Unit = {
    val topic = "weather-data"
    // Test data
    val smhiForecast = "Forecast(SMHI,2021-10-11T10:00Z,2021-10-11T11:00Z,10.8)"
    val metForecast = "Forecast(MET,2021-10-11T09:00Z,2021-10-11T10:00Z,10.4)"
    val openForecast = "Forecast(OpenWeatherMap,2021-10-11T10:00Z,2021-10-11T11:00Z,10.1)"
    val brokers = scala.util.Properties.envOrElse("BROKERS", "kafka:9092");

    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers)
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "ScalaProducerExample")
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    val producer = new KafkaProducer[String, String](props)

    
    while (true) {
        println("Sending kafka msgs");
        val uuid = UUID.randomUUID().toString();
        // Send SMHI test data
        val smhiData = new ProducerRecord[String, String](topic, uuid, smhiForecast)
        producer.send(smhiData)
        // Send Met test data
        val metData = new ProducerRecord[String, String](topic, uuid, metForecast)
        producer.send(metData)
        // Send OpenWeatherMap test data
        val openData = new ProducerRecord[String, String](topic, uuid, openForecast)
        producer.send(openData)

        println("Sent kafka msgs");

        Thread.sleep(2000);
    }

    producer.close()
  }
    
}
