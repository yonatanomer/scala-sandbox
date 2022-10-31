package crawler.handlers

import cats.effect.IO
import crawler.api.CrawlParams
import crawler.api.Schema._
import crawler.{AppContext, ContextAccessor}
import sttp.model.StatusCode

object CrawlRequestHandler extends ContextAccessor {

  def crawl(params: CrawlParams)(implicit appContext: AppContext): IO[Either[(StatusCode, String), String]] = {
    println(s"crawl handler received query :  $params")

    appContext.tasksDao
      .insertTask(params)
      .map { case Left(err) =>
        Left(new ErrorOut(sttp.model.StatusCode.InternalServerError, s"could not save task: $err"))
      }
      // note: this will catch only exceptions thrown by IO.raiseError, application errors should be caught in the statement above
      .handleError(err => {
        Left(new ErrorOut(sttp.model.StatusCode.InternalServerError, s"could not save task: $err"))
      })
  }

  // todo move this to a task consumer
//  def crawl(params: CrawlParams)(implicit appContext: AppContext): IO[Either[(StatusCode, String), String]] = {
//    println(s"crawl handler received query :  $params")
//
//    val req = Request[IO](
//      Method.GET,
//      Uri.unsafeFromString(
//        params.url
//      )
//    )
//
//    httpClient
//      .expect[String](req)
//      .flatMap(resp => {
//        if (resp.contains(params.pattern))
//          IO(Right(s"We found your pattern in \n$params\n\n" + resp))
//        else {
//          IO(Left(new ErrorOut(sttp.model.StatusCode.NotFound, s"search pattern not found")))
//        }
//      })
//      .handleError(err => {
//        Left(
//          new ErrorOut(
//            sttp.model.StatusCode.InternalServerError,
//            s"crawl handler failed due to internal server error: " + err.getClass
//          )
//        )
//      })
//  }
}
