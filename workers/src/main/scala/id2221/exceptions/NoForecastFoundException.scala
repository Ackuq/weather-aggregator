package exceptions

final case class NoForecastFoundException(message: String)
    extends Exception(message)
