package id2221.handlers

import id2221.connectors.SMHIConnector
import id2221.producers.SMHIProducer

import java.time.ZonedDateTime

class SMHIHandler extends RootHandler {
  def handleMessage(
      uuid: String,
      longitude: Double,
      latitude: Double,
      dateOption: Option[ZonedDateTime]
  ): Unit = {
    val forecast = SMHIConnector.getForecast(
      longitude.toDouble,
      latitude.toDouble,
      dateOption
    )
    SMHIProducer.sendForecast(forecast, uuid);
  }
}
