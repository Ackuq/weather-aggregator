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
    try {
      val forecast = SMHIConnector.getForecast(
        longitude.toDouble,
        latitude.toDouble,
        dateOption
      )
      SMHIProducer.sendForecast(forecast, uuid);
    } catch {
      case e: Throwable => {
        logger.warn("Failed to fetch SMHI forecast")
        SMHIProducer.sendErrorMessage(
          "Failed to get SMHI forecast information",
          uuid
        )
      }
    }
  }
}
