package crawler.api

import cats.effect.IO
import crawler.api.domain.CrawlParams
import sttp.tapir
import sttp.tapir.server.ServerEndpoint
import sttp.tapir.server.ServerEndpoint.Full
import sttp.tapir.swagger.bundle.SwaggerInterpreter
import sttp.tapir.{AnyEndpoint, EndpointInput, endpoint, query, statusCode, stringBody}

object Schema {

  type BasicEndpoint[I, O, ERR] = tapir.Endpoint[Unit, I, ERR, O, Any]
  type BasicServerEndpoint[I, O, ERR] = Full[Unit, Unit, I, ERR, O, Any, IO]

  type ErrorOut = (sttp.model.StatusCode, String)
  type Output = String

  val crawlRequestParams: EndpointInput[CrawlParams] =
    query[String]("url")
      .and(query[String]("pattern"))
      .and(query[Option[Int]]("depth"))
      .map(params => CrawlParams(params._1, params._2, params._3))(params => (params.url, params.pattern, params.depth))

  val crawlEndpointSchema: BasicEndpoint[CrawlParams, Output, ErrorOut] =
    endpoint.get
      .description("Crawler Service")
      .in("crawl")
      .in(crawlRequestParams)
      .out(stringBody)
      .errorOut(statusCode)
      .errorOut(stringBody)

  def docs(): List[ServerEndpoint[Any, IO]] = {
    val endpoints: List[AnyEndpoint] = List(crawlEndpointSchema)
    SwaggerInterpreter().fromEndpoints(endpoints, "Crawler Service", "1.0")
  }
}
