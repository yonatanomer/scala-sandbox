package crawler.api

import cats.effect.IO
import crawler.{AppContext, CrawlerApp}
import crawler.api.Schema.{BasicServerEndpoint, ErrorOut, Output, crawlEndpointSchema}
import sttp.tapir.server.ServerEndpoint

object Api {
  def crawlEndpoint(app: CrawlerApp): BasicServerEndpoint[CrawlParams, Output, ErrorOut] = {
    crawlEndpointSchema.serverLogic(app.crawl(_))
  }

  def endpoints(app: CrawlerApp): List[ServerEndpoint[Any, IO]] =
    List(crawlEndpoint(app))

}
