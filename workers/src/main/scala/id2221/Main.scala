package id2221
import id2221.handlers.{METHandler, SMHIHandler, OpenWeatherMapHandler};
object Main extends App {
  new Thread(new METHandler()).start()
  new Thread(new SMHIHandler()).start()
  new Thread(new OpenWeatherMapHandler()).start()
}
