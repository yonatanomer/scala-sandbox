package crawler

import cats.effect.IO
import com.yon.kafka.MessageProducer
import crawler.persistance.{CrawlTask, TasksDao}
import org.http4s.client.Client

case class AppContext(client: Client[IO], tasksDao: TasksDao, taskProducer: MessageProducer[String, CrawlTask])

trait ContextAccessor {
  def httpClient(implicit appContext: AppContext): Client[IO] = appContext.client
  def tasksDao(implicit appContext: AppContext) = appContext.tasksDao
  def taskProducer(implicit appContext: AppContext): MessageProducer[String, CrawlTask] = appContext.taskProducer
}
