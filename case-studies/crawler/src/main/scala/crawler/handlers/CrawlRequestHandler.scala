package crawler.handlers

import cats.Functor
import crawler.api.CrawlParams
import crawler.api.Schema._
import cats.effect.IO
import org.http4s.{Method, Request, Uri}
import org.http4s.client.Client
import sttp.model.StatusCode

trait RequestHandler[IN, OUT] {
  def crawl(client: Client[IO], params: IN): IO[Either[ErrorOut, OUT]]
}

object CrawlRequestHandler extends RequestHandler[CrawlParams, String] {
  override def crawl(client: Client[IO], params: CrawlParams): IO[Either[(StatusCode, String), String]] = {
    println(s"crawl handler received query :  $params")

    val req = Request[IO](
      Method.GET,
      Uri.unsafeFromString(
        params.url
      )
    )

    client
      .expect[String](req)
      .flatMap(resp => {
        if (resp.contains(params.pattern))
          IO(Right(s"We found your pattern in \n$params\n\n" + resp))
        else {
          IO(Left(new ErrorOut(sttp.model.StatusCode.NotFound, s"search pattern not found")))
        }
      })
      .handleError(err => {
        Left(
          new ErrorOut(
            sttp.model.StatusCode.InternalServerError,
            s"crawl handler failed due to internal server error: " + err.getClass
          )
        )
      })
  }
}
