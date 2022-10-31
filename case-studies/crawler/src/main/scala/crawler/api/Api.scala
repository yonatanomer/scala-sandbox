package crawler.api

import cats.effect.IO
import crawler.AppContext
import crawler.api.Schema.{BasicServerEndpoint, ErrorOut, Output, crawlEndpointSchema}
import crawler.handlers.CrawlRequestHandler
import sttp.tapir.server.ServerEndpoint

object Api {
  def crawlEndpoint(appContext: AppContext): BasicServerEndpoint[CrawlParams, Output, ErrorOut] =
    crawlEndpointSchema.serverLogic(CrawlRequestHandler.crawl(appContext.client, _))

  def endpoints()(implicit appContext: AppContext): List[ServerEndpoint[Any, IO]] = List(crawlEndpoint(appContext))
}
