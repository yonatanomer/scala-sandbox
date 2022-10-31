package crawler

import cats.effect.IO
import crawler.persistance.TasksDao
import org.http4s.client.Client

case class AppContext(client: Client[IO], tasksDao: TasksDao)

trait ContextAccessor {
  def httpClient(implicit appContext: AppContext): Client[IO] = appContext.client
}
