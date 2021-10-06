package controllers

import javax.inject.{Inject, Singleton}
import play.api._
import play.api.mvc._

@Singleton
class MessageController @Inject() (
    val controllerComponents: ControllerComponents
) extends BaseController {

  def getAll(): Action[AnyContent] = Action {
    NoContent
  }
}
