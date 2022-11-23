package crawler.api

import cats.effect.IO
import com.yon.kafka.CakeMessageProducer
import crawler.{AppContext, CrawlerApp}
import crawler.api.Schema.{BasicServerEndpoint, ErrorOut, Output, crawlEndpointSchema}
import crawler.handlers.CrawlRequestHandler
import crawler.persistance.{CrawlTask, MongoTasksDao}
import org.http4s.HttpRoutes
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.http4s.Http4sServerInterpreter

object Api {
  def crawlEndpoint(app: CrawlerApp): BasicServerEndpoint[CrawlParams, Output, ErrorOut] = {
    crawlEndpointSchema.serverLogic(app.crawl(_))
  }

  def endpoints(app: CrawlerApp): List[ServerEndpoint[Any, IO]] =
    List(crawlEndpoint(app))
}

trait Routes {
  this: CrawlRequestHandler =>
  def crawlEndpoint(app: CrawlerApp): BasicServerEndpoint[CrawlParams, Output, ErrorOut] = {
    crawlEndpointSchema.serverLogic(crawl(_))
  }

  def endpoints(app: CrawlerApp): List[ServerEndpoint[Any, IO]] =
    List(crawlEndpoint(app))

  def initRoutes(app: CrawlerApp): HttpRoutes[IO] = {
    Http4sServerInterpreter[IO]().toRoutes(Api.endpoints(app) ++ Schema.docs())
  }
}
