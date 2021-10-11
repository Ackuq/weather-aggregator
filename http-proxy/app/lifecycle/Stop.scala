package id2221.lifecycle

import javax.inject.Inject
import play.api.inject.ApplicationLifecycle
import id2221.producers.RequestProducer
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global;

class EnglishHello @Inject() (lifecycle: ApplicationLifecycle) {
  lifecycle.addStopHook { () =>
    Future { RequestProducer.producer.close() }
  }
}
