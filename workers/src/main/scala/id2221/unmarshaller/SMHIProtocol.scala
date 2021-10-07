package unmarshaller

import models.SMHIResponse._;
import spray.json.DefaultJsonProtocol
import models.SMHIResponse

object SMHIProtocol extends DefaultJsonProtocol {
  implicit val geometryFormat = jsonFormat2(SMHIResponse.Geometry);
  implicit val parameterFormat = jsonFormat5(SMHIResponse.Parameter);
  implicit val timeSerieFormat = jsonFormat2(SMHIResponse.TimeSerie);
  implicit val forecastResponseFormat = jsonFormat4(SMHIResponse.ForecastResponse);
}