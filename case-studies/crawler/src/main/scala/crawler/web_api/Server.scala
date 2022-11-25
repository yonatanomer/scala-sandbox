package crawler.web_api

import cats.effect.{IO, Resource}
import crawler.ServerConfig
import org.http4s.HttpRoutes
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.{Router, Server}

object Server {
  def apply(routes: HttpRoutes[IO], config: ServerConfig): Resource[IO, Server] = BlazeServerBuilder[IO]
    .bindHttp(config.port, config.host)
    .withHttpApp(Router("/" -> routes).orNotFound)
    .resource

}
