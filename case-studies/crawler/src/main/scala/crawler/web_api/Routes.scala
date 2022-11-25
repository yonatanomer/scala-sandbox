package crawler.web_api

import cats.effect.IO
import crawler.web_api.Schema.{BasicServerEndpoint, ErrorOut, Output, crawlEndpointSchema}
import crawler.web_api.domain.CrawlParams
import crawler.web_api.handlers.CrawlRequestHandler
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
