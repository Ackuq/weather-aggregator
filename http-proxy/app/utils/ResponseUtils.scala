package id2221.utils

import play.api.mvc.{Result, Results};
import play.api.libs.json._;

object ResponseUtils {
  def createResultResponse(result: JsValue): JsObject = {
    JsObject(
      Seq(
        "result" -> result
      )
    )
  }

  def createErrorResponse(
      code: Int,
      message: String
  ): JsObject = {
    JsObject(
      Seq(
        "code" -> JsNumber(code),
        "message" -> JsString(message)
      )
    )
  }
}
