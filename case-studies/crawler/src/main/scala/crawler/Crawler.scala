package crawler

import cats.effect.{ExitCode, IO, IOApp, Resource}
import com.yon.ConfigWrapper
import com.yon.db.MongoDbClient
import com.yon.kafka.{CakeMessageProducer, MessageProducer}
import crawler.api.{Api, CrawlParams, Schema}
import crawler.handlers.CrawlRequestHandler
import crawler.persistance.{CrawlTask, MongoTasksDao, TasksDao}
import io.circe.generic.auto._
import org.apache.kafka.clients.producer.KafkaProducer
import org.http4s._
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.{Router, Server}
import sttp.model.StatusCode
import sttp.tapir.server.http4s.Http4sServerInterpreter

import scala.io.StdIn

/** Application components registry
  * (IoC using Cake Pattern)
  */
// todo: crawler.api.Routes should be another dependency, the only API we will expose is initRoutes
class CrawlerApp(
    val mongo: MongoDbClient,
    val kafkaProducer: KafkaProducer[String, CrawlTask],
    val tasksTopicName: String = "submitted-crawl-tasks"
) {

  // Hide all internal members
  private lazy val impl = new CrawlRequestHandler with MongoTasksDao with CakeMessageProducer[String, CrawlTask] {
    override val mongo: MongoDbClient = mongo
    override val topic: String = tasksTopicName
    override val kafkaProducer: KafkaProducer[String, CrawlTask] = kafkaProducer
  }

  // Expose only the API methods
  def crawl(params: CrawlParams): IO[Either[(StatusCode, String), String]] = impl.crawl(params: CrawlParams)
}

object Crawler extends IOApp {

  def initRoutes(app: CrawlerApp): HttpRoutes[IO] = {
    Http4sServerInterpreter[IO]().toRoutes(Api.endpoints(app) ++ Schema.docs())
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
