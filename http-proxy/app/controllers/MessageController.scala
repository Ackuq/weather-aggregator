package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import java.util.{Date, Properties}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord, ProducerConfig}
import kafka.producer.KeyedMessage

@Singleton
class MessageController @Inject() (
    val controllerComponents: ControllerComponents
) extends BaseController {

  def getAll(): Action[AnyContent] = Action {
    
    sendRequest();
    Ok;
  }

  def sendRequest(): Unit = {
    val topic = "request";
    val brokers = "http://kafka:9092";

    val props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokers);
    props.put(ProducerConfig.CLIENT_ID_CONFIG, "HttpProxyProducer");
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
    val producer = new KafkaProducer[String, String](props);
    val data = new ProducerRecord[String, String](topic, "key", "value");
    producer.send(data);
  }
}
