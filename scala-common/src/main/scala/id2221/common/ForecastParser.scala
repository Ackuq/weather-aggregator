package id2221.common;

import java.time.ZonedDateTime;


object ForecastParser{

    def toString(forecast: Forecast): String = {
        s"Forecast(${forecast.provider},${forecast.timeFrom},${forecast.timeTo},${forecast.temperature})";
    }

    def toForecast(forecastString: String): Forecast = {
      //val forecastRegex = """Forecast\((.*)\)""".r;
      val forecast = forecastString.slice(9, forecastString.length()-1).split(',');
      val provider = forecast(0) match {
        case "SMHI" => Provider.SMHI;
        case "MET" => Provider.MET;
        case "OpenWeatherMap" => Provider.OpenWeatherMap;
      }

      Forecast(
        provider,
        ZonedDateTime.parse(forecast(1)),
        ZonedDateTime.parse(forecast(2)),
        forecast(3).toDouble
      );
         
    }
}
