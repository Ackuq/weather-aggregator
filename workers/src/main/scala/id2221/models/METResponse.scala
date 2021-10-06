package models

object METResponse {
  final case class TimeSeriesDataInstantDetails(
      air_pressure_at_sea_level: Option[Double],
      air_temperature: Option[Double],
      air_temperature_max: Option[Double],
      air_temperature_min: Option[Double],
      cloud_area_fraction: Option[Double],
      cloud_area_fraction_high: Option[Double],
      cloud_area_fraction_low: Option[Double],
      cloud_area_fraction_medium: Option[Double],
      dew_point_temperature: Option[Double],
      fog_area_fraction: Option[Double],
      precipitation_amount: Option[Double],
      precipitation_amount_max: Option[Double],
      precipitation_amount_min: Option[Double],
      probability_of_precipitation: Option[Double],
      probability_of_thunder: Option[Double],
      relative_humidity: Option[Double],
      ultraviolet_index_clear_sky: Option[Double],
      wind_from_direction: Option[Double],
      wind_speed: Option[Double],
      wind_speed_of_gust: Option[Double]
  );
  final case class TimeSeriesDataInstant(details: TimeSeriesDataInstantDetails);
  final case class TimeSeriesFutureSummary(symbol_code: String);
  final case class TimeSeriesDataFuture(
      summary: TimeSeriesFutureSummary,
      details: TimeSeriesDataInstantDetails
  );
  final case class TimeSeriesData(
      instant: TimeSeriesDataInstant,
      next_12_hours: Option[TimeSeriesDataFuture],
      next_6_hours: Option[TimeSeriesDataFuture],
      next_1_hours: Option[TimeSeriesDataFuture]
  );
  final case class TimeSeries(time: String, data: TimeSeriesData)
  final case class Properties(timeseries: List[TimeSeries]);
  final case class Geometry(`type`: String, coordinates: List[Double])
  final case class ForecastResponse(
      `type`: String,
      geometry: Geometry,
      properties: Properties
  );
}
