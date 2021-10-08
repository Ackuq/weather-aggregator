package id2221.models

object SMHIResponse {

  case class Geometry(`type`: String, coordinates: List[List[Double]]);
  case class Parameter(
      name: String,
      levelType: String,
      level: Int,
      unit: String,
      values: List[Double]
  )
  case class TimeSerie(validTime: String, parameters: List[Parameter]);
  case class ForecastResponse(
      approvedTime: String,
      referenceTime: String,
      geometry: Geometry,
      timeSeries: List[TimeSerie]
  );
}
