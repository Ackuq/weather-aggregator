package models

object OpenWeatherMapResponse {
  final case class WeatherObject(
      id: Int,
      main: String,
      description: String,
      icon: String
  )
  final case class HourlyForecast(
      dt: Int,
      temp: Double,
      feels_like: Double,
      pressure: Int,
      humidity: Int,
      dew_point: Double,
      uvi: Double,
      clouds: Int,
      visibility: Int,
      wind_speed: Double,
      wind_deg: Int,
      wind_gust: Double,
      weather: List[WeatherObject],
      pop: Double
  )

  final case class ForecastResponse(
      lat: Double,
      lon: Double,
      timezone: String,
      timezone_offset: Int,
      hourly: List[HourlyForecast]
  )
}
