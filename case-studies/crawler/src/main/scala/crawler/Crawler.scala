package crawler

import cats.effect.{ExitCode, IO, IOApp, Resource}
import com.yon.db.MongoDbClient
import com.yon.kafka.MessageProducer
import crawler.api.{Api, Schema}
import crawler.persistance.{CrawlTask, MongoTasksDao}
import io.circe.generic.auto._
import org.http4s._
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.{Router, Server}
import sttp.tapir.server.http4s.Http4sServerInterpreter

import scala.io.StdIn

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

    // TODO: move config props to config class, move messaging constants to messaging model
    for {
      client <- BlazeClientBuilder[IO].resource
      mongo <- MongoDbClient.init("mongodb://test:test@0.0.0.0:27017", MongoTasksDao.codecs)
      tasksProducer <- MessageProducer[String, CrawlTask]("crawler", "submitted-crawl-tasks")
      server <- BlazeServerBuilder[IO]
        .bindHttp(9999, "localhost")
        .withHttpApp(Router("/" -> initRoutes(AppContext(client, MongoTasksDao(mongo), tasksProducer))).orNotFound)
        .resource
    } yield server

  }
}
