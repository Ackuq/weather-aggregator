package id2221.common;

import java.time.ZonedDateTime;

case class Forecast(
    provider: Provider.Provider,
    timeFrom: ZonedDateTime,
    timeTo: ZonedDateTime,
    temperature: Double
)
