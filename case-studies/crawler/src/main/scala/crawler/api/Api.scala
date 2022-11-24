package crawler.api

import cats.effect.IO
import crawler.api.Schema.{BasicServerEndpoint, ErrorOut, Output, crawlEndpointSchema}
import crawler.handlers.CrawlRequestHandler
import org.http4s.HttpRoutes
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter

trait Routes {
  this: CrawlRequestHandler =>
  def crawlEndpoint: BasicServerEndpoint[CrawlParams, Output, ErrorOut] = {
    crawlEndpointSchema.serverLogic(crawl(_))
  }

  def endpoints: List[ServerEndpoint[Any, IO]] =
    List(crawlEndpoint)

  def initRoutes: HttpRoutes[IO] = {
    Http4sServerInterpreter[IO]().toRoutes(endpoints ++ Schema.docs)
  }

}
