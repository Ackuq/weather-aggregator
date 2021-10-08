package id2221.connectors;

import id2221.models.METResponse;
import id2221.exceptions.NoForecastFoundException;
import id2221.common.{Forecast, Provider}
import id2221.unmarshaller.METProtocol._;

import akka.http.scaladsl.Http;
import akka.http.scaladsl.model.Uri;
import akka.http.scaladsl.unmarshalling.Unmarshal;
import akka.http.scaladsl.client.RequestBuilding.Get;
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.sprayJsonUnmarshaller;
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors

import scala.language.postfixOps;
import scala.concurrent.Await;
import scala.concurrent.duration.{DurationInt};

import java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import javax.print.attribute.standard.MediaSize.ISO
import java.time.ZonedDateTime
import org.slf4j.LoggerFactory;

object METConnector extends Connector {
  final val logger = LoggerFactory.getLogger(METConnector.getClass().getName());
  final val baseURL = Uri(
    "https://api.met.no/weatherapi/locationforecast/2.0/complete"
  );

  private def findForecastFromDate(
      date: ZonedDateTime,
      forecastResponse: METResponse.ForecastResponse
  ): (
      METResponse.TimeSeries,
      METResponse.TimeSeries
  ) = {
    val forecastPairList =
      forecastResponse.properties.timeseries
        .sliding(2)
        .find(interval => {
          // All the time series objects has a timestamp from when they start, get the stop timestamp from the next element in the list
          assert(interval.length == 2);
          val fromA = ZonedDateTime.parse(interval(0).time, ISO_DATE_TIME);
          val fromB = ZonedDateTime.parse(interval(1).time, ISO_DATE_TIME);
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
    implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")
    logger.info("Fetching forecast...");

    val url =
      baseURL.withQuery(
        Uri
          .Query("lon" -> longitude.toString(), "lat" -> latitude.toString())
      );

    val response = Await.result(
      Http().singleRequest(Get(url)),
      2 seconds
    );

    val forecastResponseFuture =
      Unmarshal(response.entity).to[METResponse.ForecastResponse];
    val forecastResponse = Await.result(forecastResponseFuture, 2 seconds)

    val (from, to) = date match {
      case Some(dateTime) =>
        findForecastFromDate(dateTime, forecastResponse)
      // If date is not set, just get the current forecast
      case None =>
        (
          forecastResponse.properties.timeseries(0),
          forecastResponse.properties.timeseries(1)
        )
    }

    val temperature = from.data.instant.details.air_temperature.get;
    val forecast = Forecast(
      Provider.MET,
      ZonedDateTime.parse(from.time, ISO_DATE_TIME),
      ZonedDateTime.parse(to.time, ISO_DATE_TIME),
      temperature
    );

    system.terminate();
    forecast
  }
}
