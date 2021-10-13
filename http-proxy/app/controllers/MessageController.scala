package id2221.controllers;

import id2221.common.Payload;
import id2221.producers.RequestProducer;
import id2221.consumers.ResultConsumer;
import id2221.utils.ResponseUtils._;
import play.api._;
import play.api.mvc._;
import play.api.libs.json._;

import org.apache.kafka.clients.producer.{
  KafkaProducer,
  ProducerRecord,
  ProducerConfig
}
import javax.inject._;
import java.util.{Date, Properties};
import java.util.UUID;

import play.api.cache._
import scala.util.{Success, Failure, Try};
import scala.concurrent.ExecutionContext.Implicits.global;
import scala.concurrent.duration.DurationInt;
import scala.concurrent.{Await, Future, TimeoutException};

import org.slf4j.LoggerFactory;

@Singleton
class MessageController @Inject() (
    val cache: SyncCacheApi,
    val controllerComponents: ControllerComponents
) extends BaseController {
  val consumerThread = new Thread(new ResultConsumer(cache));
  consumerThread.start();

  final val logger = LoggerFactory.getLogger(this.getClass().getName());

  def awaitForecast(uuid: String): Future[String] = Future {
    var result: Option[String] = None
    while ({ result = cache.get(uuid); result.isEmpty }) {
      Thread.sleep(100);
    }
    val value = result.get;
    logger.info(s"Got result $value for uuid $uuid")
    return Future.successful(result.get);
  }

  def getForecast(lat: Double, lng: Double) = Action.async {
    Future {
      try {
        val payload = Payload(lng, lat, None);
        val uuid = UUID.randomUUID().toString();
        RequestProducer.requestData(payload, uuid);
        val response = Await.ready(awaitForecast(uuid), 10.seconds);

        response.value.get match {
          case Success(value) => {
            // TODO: Handle return value?
            Ok(createResultResponse(JsString(value)))
          }
          case Failure(exception) => {
            logger.warn(
              s"Process with UUID $uuid failed with exception: ${exception.getMessage()}"
            )
            InternalServerError(
              createErrorResponse(
                InternalServerError.header.status,
                "Something went wrong"
              )
            )
          }
        }
      } catch {
        case exception: TimeoutException => {
          logger.warn(s"Handling of consumer took too long to process");
          InternalServerError(
            createErrorResponse(
              InternalServerError.header.status,
              "Took too long to process"
            )
          )
        }
        case exception: Throwable => {
          logger.warn(
            s"Something went wrong when processing request, failed with exception: ${exception.getMessage()}"
          )
          InternalServerError(
            createErrorResponse(
              InternalServerError.header.status,
              "Something went wrong"
            )
          )
        }
      }
    }
  }

}
