package crawler

import crawler.api.Schema
import crawler.api.Api

import cats.effect.{ExitCode, IO, IOApp, Resource}
import org.http4s._
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.client.Client
import org.http4s.server.{Router, Server}
import sttp.tapir.server.http4s.Http4sServerInterpreter

import scala.io.StdIn

case class AppContext(client: Client[IO])

object Crawler extends IOApp {

  def initRoutes(implicit appContext: AppContext): HttpRoutes[IO] = {
    Http4sServerInterpreter[IO]().toRoutes(Api.endpoints() ++ Schema.docs())
  }

  override def run(args: List[String]): IO[ExitCode] = {

    startServer
      .use { _ =>
        IO.blocking {
          println(s"Server on port 9999")
          println(s"Press any key to exit.")
          StdIn.readLine()
        }
      }
      .as(ExitCode.Success) // same as .map(_ => ExitCode.Success)
  }

  def startServer: Resource[IO, Server] = {
    for {
      client <- BlazeClientBuilder[IO].resource
      server <- BlazeServerBuilder[IO]
        .bindHttp(9999, "localhost")
        .withHttpApp(Router("/" -> initRoutes(AppContext(client))).orNotFound)
        .resource
    } yield server

  }
}
