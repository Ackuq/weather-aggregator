package id2221.exceptions

final case class NoForecastFoundException(message: String)
    extends Exception(message)
