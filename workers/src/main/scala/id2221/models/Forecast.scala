package models;

import java.time.ZonedDateTime;

case class Forecast(
    provider: Provider.Provider,
    timeFrom: ZonedDateTime,
    timeTo: ZonedDateTime,
    temperature: Double
)
