package crawler.api

import cats.effect.IO
import crawler.{AppContext, CrawlerApp}
import crawler.api.Schema.{BasicServerEndpoint, ErrorOut, Output, crawlEndpointSchema}
import crawler.handlers.CrawlRequestHandler
import sttp.tapir.server.ServerEndpoint

object Api {
  def crawlEndpoint(implicit appContext: AppContext): BasicServerEndpoint[CrawlParams, Output, ErrorOut] = {
    crawlEndpointSchema.serverLogic(CrawlRequestHandler.crawl(_))
  }

  def endpoints()(implicit appContext: AppContext): List[ServerEndpoint[Any, IO]] =
    List(crawlEndpoint)

  def cakeCrawlEndpoint(app: CrawlerApp): BasicServerEndpoint[CrawlParams, Output, ErrorOut] = {
    crawlEndpointSchema.serverLogic(app.crawl(_))
  }

  def cakeEndpoints(app: CrawlerApp): List[ServerEndpoint[Any, IO]] =
    List(cakeCrawlEndpoint(app))

}
