package crawler

import cats.effect.{ExitCode, IO, IOApp, Resource}
import com.yon.ConfigWrapper
import com.yon.db.MongoDbClient
import com.yon.kafka.MessageProducer
import crawler.web_api.Server
import crawler.persistance.MongoTasksDao
import crawler.persistance.domain.CrawlTask
import io.circe.generic.auto._
import org.apache.kafka.clients.producer.KafkaProducer
import org.http4s.blaze.client.BlazeClientBuilder
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.{HttpRoutes, server}
import org.http4s.server.{Router, Server}

import scala.io.StdIn

object Crawler extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    startServer
      .use { _ =>
        IO.blocking {
          println(s"Server on port 9999")
          println(s"Press any key to exit.")
          StdIn.readLine()
        }
      }
      .as(ExitCode.Success)
  }

  def startServer: Resource[IO, server.Server] = {
    for {
      conf <- Config.make
      client <- BlazeClientBuilder[IO].resource
      mongo <- MongoDbClient.make(conf.mongo, MongoTasksDao.codecs)
      tasksProducer <- MessageProducer.kafkaProducer[String, CrawlTask](conf.kafka.clientId)
      server <- Server(AppContext.initRoutes(mongo, tasksProducer), conf.server)
    } yield server

  }
}
