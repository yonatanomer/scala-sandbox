package crawler

import cats.effect.IO
import com.yon.db.MongoDbClient
import com.yon.kafka.CakeMessageProducer
import crawler.api.Routes
import crawler.handlers.CrawlRequestHandler
import crawler.persistance.{CrawlTask, MongoTasksDao}
import org.apache.kafka.clients.producer.KafkaProducer
import org.http4s.HttpRoutes

/** IoC using cake pattern */
class AppContext(
    override val mongo: MongoDbClient,
    override val kafkaProducer: KafkaProducer[String, CrawlTask],
    override val topic: String = "submitted-crawl-tasks"
) extends Routes
    with MongoTasksDao
    with CrawlRequestHandler
    with CakeMessageProducer[String, CrawlTask]

object AppContext {
  def init(mongo: MongoDbClient, kafkaProducer: KafkaProducer[String, CrawlTask]): HttpRoutes[IO] = {
    val app = new AppContext(mongo, kafkaProducer)
    app.initRoutes()
  }
}
