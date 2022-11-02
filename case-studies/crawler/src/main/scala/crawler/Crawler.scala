package crawler

import cats.effect.{ExitCode, IO, IOApp, Resource}
import com.yon.db.MongoDbClient
import crawler.api.{Api, Schema}
import crawler.handlers.CrawlRequestHandler
import crawler.persistance.{CrawlTask, MongoTasksDao}
import crawler.producers.KafkaTaskProducer
import org.bson.codecs.configuration.CodecProvider
import org.http4s._
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.{Router, Server}
import sttp.tapir.server.http4s.Http4sServerInterpreter

import scala.collection.immutable.Seq
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
    sfdsf // todo MongoTasksDao will be a class and will have mongo as a member

    for {
      client <- BlazeClientBuilder[IO].resource
      mongo <- MongoDbClient[IO]("todo url", MongoTasksDao.codecs).resource
      server <- BlazeServerBuilder[IO]
        .bindHttp(9999, "localhost")
        .withHttpApp(Router("/" -> initRoutes(AppContext(client, mongo, MongoTasksDao, KafkaTaskProducer))).orNotFound)
        .resource
    } yield server

  }
}
