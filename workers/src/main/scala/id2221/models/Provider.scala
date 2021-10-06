package models;

object Provider extends Enumeration {
  type Provider = String;
  val SMHI = "SMHI";
  val MET = "MET";
  val OpenWeatherMap = "OpenWeatherMap"
}
