package crawler.utils

import crawler.api.Schema.ErrorOut
import sttp.model.StatusCode

object ErrorUtils {

  def errorResponse(code: StatusCode, message: String): Left[(StatusCode, String), Nothing] = {
    val methodName = Thread.currentThread.getStackTrace()(3).getMethodName
    println(s"responding with error in $methodName: $message")
    Left(new ErrorOut(code, message))
  }
}
