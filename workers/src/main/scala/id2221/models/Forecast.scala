package models;

import java.time.LocalDateTime;

case class Forecast(
    provider: Provider.Provider,
    timeFrom: LocalDateTime,
    timeTo: LocalDateTime,
    temperature: Double
)
