package id2221.common;

import java.time.ZonedDateTime

final case class Payload(
    longitude: Double,
    latitude: Double,
    date: Option[ZonedDateTime]
)
