package id2221.handlers

import id2221.connectors.METConnector
import id2221.producers.METProducer

import java.time.ZonedDateTime

class METHandler extends RootHandler {
  def handleMessage(
      uuid: String,
      longitude: Double,
      latitude: Double,
      dateOption: Option[ZonedDateTime]
  ): Unit = {
    val forecast = METConnector.getForecast(
      longitude.toDouble,
      latitude.toDouble,
      dateOption
    )
    METProducer.sendForecast(forecast, uuid);
  }
}
