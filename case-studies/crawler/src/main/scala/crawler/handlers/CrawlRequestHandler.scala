package crawler.handlers

import cats.data.EitherT
import cats.effect.IO
import com.yon.kafka.MessageProducer
import crawler.api.domain.CrawlParams
import crawler.persistance.MongoTasksDao
import crawler.persistance.domain.CrawlTask
import crawler.utils.ErrorUtils
import sttp.model.StatusCode
import crawler.messaging.Messaging.crawlTasksTopic

trait CrawlRequestHandler {
  this: MongoTasksDao with MessageProducer[String, CrawlTask] =>

  def crawl(params: CrawlParams): IO[Either[(StatusCode, String), String]] = {
    val ret = for {
      task <- EitherT(insertTask(params))
      res <- EitherT(send(crawlTasksTopic, task.id.toString, task))
    } yield res

    ret.fold(
      err => ErrorUtils.errorResponse(sttp.model.StatusCode.InternalServerError, s"could not save task: $err"),
      _ => Right("task submitted")
    )
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
