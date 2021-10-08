package id2221.connectors;

import id2221.common.{Provider, Forecast};
import id2221.models.SMHIResponse.{ForecastResponse, TimeSerie};
import id2221.exceptions.NoForecastFoundException;
import id2221.unmarshaller.SMHIProtocol._;

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

import java.time.{ZonedDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter._

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http._
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport.sprayJsonUnmarshaller;

import spray.json._
import org.slf4j.LoggerFactory;

object SMHIConnector extends Connector {
  final val logger = LoggerFactory.getLogger(METConnector.getClass().getName());

  private def findForecastFromDate(
      date: ZonedDateTime,
      forecastResponse: ForecastResponse
  ): (
      TimeSerie,
      TimeSerie
  ) = {
    val forecastPairList = forecastResponse.timeSeries
      .sliding(2)
      .find(interval => {
        // All the time series objects has a timestamp from when they start, get the stop timestamp from the next element in the list
        assert(interval.length == 2);
        val from = ZonedDateTime
          .parse(interval(0).validTime, ISO_DATE_TIME)
        val to = ZonedDateTime
          .parse(interval(1).validTime, ISO_DATE_TIME)

        (date.isEqual(from)) ||
        (date.isAfter(from) && date.isBefore(to))
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
    implicit val system = ActorSystem(Behaviors.empty, "SingleRequest")

    val requestUrl =
      s"https://opendata-download-metfcst.smhi.se/api/category/pmp3g/version/2/geotype/point/lon/${longitude}/lat/${latitude}/data.json";

    val response: HttpResponse = Await.result(
      Http().singleRequest(HttpRequest(uri = requestUrl)),
      2 seconds
    );

    val parsedResponse: ForecastResponse =
      Await.result(Unmarshal(response.entity).to[ForecastResponse], 2 seconds);

    val (from, to) = date match {
      case Some(dateTime) =>
        findForecastFromDate(dateTime, parsedResponse)
      // If date is not set, just get the current forecast
      case None =>
        (parsedResponse.timeSeries(0), parsedResponse.timeSeries(1))
    }

    // Find the temperature value, if temp parameter is not found, return a default (0.0)
    val temperature = from.parameters
      .find(p => p.name == "t")
      .get
      .values
      .head

    val forecast = Forecast(
      Provider.SMHI,
      ZonedDateTime
        .parse(from.validTime, ISO_DATE_TIME),
      ZonedDateTime
        .parse(to.validTime, ISO_DATE_TIME),
      temperature
    );
    system.terminate();
    return forecast;
  }
}
