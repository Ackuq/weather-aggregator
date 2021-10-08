package id2221.unmarshaller

import id2221.models.METResponse._;
import spray.json.DefaultJsonProtocol

object METProtocol extends DefaultJsonProtocol {
  implicit val timeSeriesDataInstantDetailsFormat = jsonFormat20(
    TimeSeriesDataInstantDetails
  );
  implicit val timeSeriesDataInstantFormat = jsonFormat1(TimeSeriesDataInstant);
  implicit val timeSeriesFutureSummaryFormat = jsonFormat1(
    TimeSeriesFutureSummary
  );
  implicit val timeSeriesDataFutureFormat = jsonFormat2(TimeSeriesDataFuture);
  implicit val timeSeriesDataFormat = jsonFormat4(TimeSeriesData);
  implicit val timeSeriesFormat = jsonFormat2(TimeSeries);
  implicit val propertiesFormat = jsonFormat1(Properties)
  implicit val geometryFormat = jsonFormat2(Geometry);
  implicit val forecastResponseFormat = jsonFormat3(ForecastResponse);
}
