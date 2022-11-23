package crawler

import cats.effect.{ExitCode, IO, IOApp, Resource}
import com.yon.ConfigWrapper
import com.yon.db.MongoDbClient
import com.yon.kafka.{CakeMessageProducer, MessageProducer}
import crawler.api.{Api, CrawlParams, Schema}
import crawler.handlers.CrawlRequestHandlerCake
import crawler.persistance.{CrawlTask, MongoTasksDao, MongoTasksDaoCake, TasksDao}
import io.circe.generic.auto._
import org.apache.kafka.clients.producer.KafkaProducer
import org.http4s._
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.{Router, Server}
import sttp.tapir.server.http4s.Http4sServerInterpreter

import scala.io.StdIn

class CrawlerApp(
    override val mongo: MongoDbClient,
    override val kafkaProducer: KafkaProducer[String, CrawlTask],
    override val topic: String = "submitted-crawl-tasks"
) extends MongoTasksDaoCake
    with CrawlRequestHandlerCake
    with CakeMessageProducer[String, CrawlTask] {}

object CrawlerCakePattern extends IOApp {

  def initRoutes(app: CrawlerApp): HttpRoutes[IO] = {
    Http4sServerInterpreter[IO]().toRoutes(Api.cakeEndpoints(app) ++ Schema.docs())
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
      conf <- ConfigWrapper.make("crawler.conf")
      client <- BlazeClientBuilder[IO].resource
      mongo <- MongoDbClient.make(conf, MongoTasksDao.codecs)
      tasksProducer <- MessageProducer.kafkaProducer[String, CrawlTask]("crawler")
      server <- BlazeServerBuilder[IO]
        .bindHttp(9999, "localhost")
        .withHttpApp(Router("/" -> initRoutes(new CrawlerApp(mongo, tasksProducer))).orNotFound)
        .resource
    } yield server

  }
}
