package connectors

import java.time.ZonedDateTime
import models.Forecast

trait Connector {

  /** Get the forecast for a specific location on a specific date
    *
    * @param longitude
    * @param latitude
    * @param date
    *   Optional, a date in UTC, if none is set, the current date is set
    * @return
    *   A forecast object with the fetched data
    */
  def getForecast(
      longitude: Double,
      latitude: Double,
      date: Option[ZonedDateTime]
  ): Forecast
}
