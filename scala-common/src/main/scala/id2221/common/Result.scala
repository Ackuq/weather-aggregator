import java.time.ZonedDateTime

final case class Result(
    timeFrom: ZonedDateTime,
    timeTo: ZonedDateTime,
    averageTemperature: Double
)
