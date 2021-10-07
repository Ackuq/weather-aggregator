package connectors;
import models.Forecast;
import models.Provider;
import models.SMHIResponse.ForecastResponse;
import akka.http._

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{ Failure, Success }
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.sprayJsonUnmarshaller;
import unmarshaller.SMHIProtocol._;
import spray.json._
import java.time.format.DateTimeFormatter._
import java.time.LocalDateTime
import models.SMHIResponse

object SMHIConnector {

  def getForecast(long: Double, lat: Double) : Forecast = {
    val requestUrl = s"https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/${long}/lat/${lat}/data.json";
    
    implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
    implicit val executionContext = system.executionContext

    val response: HttpResponse = Await.result(Http().singleRequest(HttpRequest(uri = requestUrl)), 2 seconds);

    val parsedResponse: ForecastResponse = Await.result(Unmarshal(response.entity).to[ForecastResponse], 2 seconds);

    val currentTimeForecast = parsedResponse.timeSeries(0);
    // Find the temperature value, if temp parameter is not found, return a default (0.0)
    // TODO: change Forecast parameters to Options!
    val temperature = currentTimeForecast.parameters.find(p => p.name == "t").getOrElse(SMHIResponse.Parameter("", "", 0, "", List(0.0))).values(0);

    val forecast = Forecast(
      Provider.SMHI, 
      LocalDateTime.now(),
      LocalDateTime.parse(currentTimeForecast.validTime, ISO_DATE_TIME),
      temperature
    );
    println(s"Received SMHI Forecast: ${forecast}")
    return forecast;
  }
}