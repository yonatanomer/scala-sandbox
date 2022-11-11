package crawler

import cats.effect.IO
import com.yon.db.MongoDbClient
import crawler.persistance.TasksDao
import crawler.producers.TaskProducer
import org.http4s.client.Client

case class AppContext(client: Client[IO], tasksDao: TasksDao, taskProducer: TaskProducer)

trait ContextAccessor {
  def httpClient(implicit appContext: AppContext): Client[IO] = appContext.client
  def tasksDao(implicit appContext: AppContext) = appContext.tasksDao
  def taskProducer(implicit appContext: AppContext) = appContext.taskProducer
}
