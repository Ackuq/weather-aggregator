package connectors;

import models.{METResponse, Forecast, Provider};
import exceptions.NoForecastFoundException;
import scala.language.postfixOps;

import akka.http.scaladsl.Http;
import akka.http.scaladsl.model.Uri;
import akka.http.scaladsl.unmarshalling.Unmarshal;
import akka.http.scaladsl.client.RequestBuilding.Get;
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.sprayJsonUnmarshaller;
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.Await;
import scala.concurrent.duration.{DurationInt};

import unmarshaller.METProtocol._;
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import javax.print.attribute.standard.MediaSize.ISO

object METConnector {
  final val baseURL = Uri(
    "https://api.met.no/weatherapi/locationforecast/2.0/complete"
  );

  def getForecast(
      longitude: Double,
      latitude: Double,
      date: LocalDateTime = LocalDateTime.now()
  ): Forecast = {
    implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")

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
    val forecastPair =
      forecastResponse.properties.timeseries
        .sliding(2)
        .find(interval => {
          // All the time series objects has a timestamp from when they start, get the stop timestamp from the next element in the list
          assert(interval.length == 2);
          val fromA = LocalDateTime.parse(interval(0).time, ISO_DATE_TIME);
          val fromB = LocalDateTime.parse(interval(1).time, ISO_DATE_TIME);
          (date.isEqual(fromA)) ||
          (date.isAfter(fromA) && date.isBefore(fromB))
        })

    val forecast = forecastPair match {
      case None =>
        throw NoForecastFoundException(
          "No forecast found for the specified date"
        )
      case Some(value) =>
        val temperature = value(0).data.instant.details.air_temperature.get;
        Forecast(
          Provider.MET,
          LocalDateTime.parse(value(0).time, ISO_DATE_TIME),
          LocalDateTime.parse(value(1).time, ISO_DATE_TIME),
          temperature
        );
    }

    system.terminate();
    forecast
  }
}
