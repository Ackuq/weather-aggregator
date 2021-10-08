package id2221.handlers

import id2221.connectors.OpenWeatherMap;
import id2221.producers.OpenWeatherMapProducer;

import org.apache.kafka.clients.consumer.KafkaConsumer
import java.time.ZonedDateTime

class OpenWeatherMapHandler extends RootHandler {
  def handleMessage(
      uuid: String,
      longitude: Double,
      latitude: Double,
      dateOption: Option[ZonedDateTime]
  ): Unit = {
    val forecast = OpenWeatherMap.getForecast(
      longitude.toDouble,
      latitude.toDouble,
      dateOption
    )
    OpenWeatherMapProducer.sendForecast(forecast, uuid);
  }
}
