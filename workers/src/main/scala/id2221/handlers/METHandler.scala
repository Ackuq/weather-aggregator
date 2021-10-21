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
    try {
      val forecast = METConnector.getForecast(
        longitude.toDouble,
        latitude.toDouble,
        dateOption
      )
      METProducer.sendForecast(forecast, uuid);
    } catch {
      case e: Throwable => {
        logger.warn("Failed to fetch MET forecast")
        METProducer.sendErrorMessage(
          "Failed to get MET forecast information",
          uuid
        )
      }
    }
  }
}
