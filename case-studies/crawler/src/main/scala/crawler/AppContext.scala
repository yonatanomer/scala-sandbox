package crawler

import cats.effect.IO
import com.yon.db.MongoDbClient
import com.yon.kafka.MessageProducer
import crawler.api.Routes
import crawler.handlers.CrawlRequestHandler
import crawler.persistance.MongoTasksDao
import crawler.persistance.domain.CrawlTask
import org.apache.kafka.clients.producer.KafkaProducer
import org.http4s.HttpRoutes

/** IoC using cake pattern */
class AppContext(
    override val mongo: MongoDbClient,
    override val kafkaProducer: KafkaProducer[String, CrawlTask]
) extends Routes
    with MongoTasksDao
    with CrawlRequestHandler
    with MessageProducer[String, CrawlTask]

object AppContext {
  def initRoutes(mongo: MongoDbClient, kafkaProducer: KafkaProducer[String, CrawlTask]): HttpRoutes[IO] = {
    val app = new AppContext(mongo, kafkaProducer)
    app.initRoutes
  }
}
