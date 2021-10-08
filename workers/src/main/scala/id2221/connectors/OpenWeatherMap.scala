package id2221.connectors;

import java.time.{ZonedDateTime, Instant, ZoneOffset};
import scala.util.Properties;
import scala.concurrent.Await;
import scala.concurrent.duration.{DurationInt};
import scala.language.postfixOps;

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.client.RequestBuilding.Get;
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.sprayJsonUnmarshaller;

import id2221.models.OpenWeatherMapResponse;
import id2221.common.{Forecast, Provider};
import id2221.unmarshaller.OpenWeatherMapProtocol._;
import id2221.exceptions.NoForecastFoundException;
import org.slf4j.LoggerFactory;

object OpenWeatherMap extends Connector {
  final val logger = LoggerFactory.getLogger(METConnector.getClass().getName());
  final val API_KEY = Properties.envOrNone("OWM_API_KEY");

  final val baseURL = Uri("https://api.openweathermap.org/data/2.5/onecall")

  private def findForecastFromDate(
      date: ZonedDateTime,
      forecastResponse: OpenWeatherMapResponse.ForecastResponse
  ): (
      OpenWeatherMapResponse.HourlyForecast,
      OpenWeatherMapResponse.HourlyForecast
  ) = {
    val forecastPairList = forecastResponse.hourly
      .sliding(2)
      .find(interval => {
        // All the time series objects has a timestamp from when they start, get the stop timestamp from the next element in the list
        assert(interval.length == 2);
        val fromA = ZonedDateTime
          .ofInstant(Instant.ofEpochSecond(interval(0).dt), ZoneOffset.UTC)
        println(fromA)
        val fromB = ZonedDateTime
          .ofInstant(Instant.ofEpochSecond(interval(1).dt), ZoneOffset.UTC)

        (date.isEqual(fromA)) ||
        (date.isAfter(fromA) && date.isBefore(fromB))
      })
    forecastPairList match {
      case Some(List(a, b)) =>
        (a, b)
      case _ =>
        throw NoForecastFoundException(
          "No forecast found for the specified date"
        )
    }
  }

  def getForecast(
      longitude: Double,
      latitude: Double,
      date: Option[ZonedDateTime] = None
  ): Forecast = {
    logger.info("Fetching forecast...");
    if (API_KEY.isEmpty) {
      throw new Exception(
        "Must specify OpenWeatherMap API key by setting the OWM_API_KEY environment variable"
      )
    }
    implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")

    val url =
      baseURL.withQuery(
        Uri
          .Query(
            "lon" -> longitude.toString(),
            "lat" -> latitude.toString(),
            "exclude" -> "current,minutely,daily,alerts",
            "appid" -> API_KEY.get,
            "units" -> "metric"
          )
      );

    val response = Await.result(
      Http().singleRequest(Get(url)),
      2 seconds
    );

    val forecastResponseFuture =
      Unmarshal(response.entity).to[OpenWeatherMapResponse.ForecastResponse];
    val forecastResponse = Await.result(forecastResponseFuture, 2 seconds)

    val (from, to) = date match {
      case Some(dateTime) =>
        findForecastFromDate(dateTime, forecastResponse)

      // If date is not set, just get the current forecast
      case None =>
        (forecastResponse.hourly(0), forecastResponse.hourly(1))
    }

    val forecast = Forecast(
      Provider.OpenWeatherMap,
      ZonedDateTime
        .ofInstant(Instant.ofEpochSecond(from.dt), ZoneOffset.UTC),
      ZonedDateTime
        .ofInstant(
          Instant.ofEpochSecond(to.dt),
          ZoneOffset.UTC
        ),
      from.temp
    );

    system.terminate();
    forecast
  }
}
