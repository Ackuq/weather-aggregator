package id2221.unmarshaller

import id2221.models.OpenWeatherMapResponse;
import spray.json.DefaultJsonProtocol

object OpenWeatherMapProtocol extends DefaultJsonProtocol {
  implicit val weatherObjectFormat = jsonFormat4(
    OpenWeatherMapResponse.WeatherObject
  )
  implicit val hourlyForecastFormat = jsonFormat14(
    OpenWeatherMapResponse.HourlyForecast
  );
  implicit val forecastResponseFormat = jsonFormat5(
    OpenWeatherMapResponse.ForecastResponse
  );
}
