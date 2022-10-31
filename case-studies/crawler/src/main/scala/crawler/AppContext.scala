package crawler

import cats.effect.IO
import org.http4s.client.Client

case class AppContext(client: Client[IO])

trait ContextAccessor {
  def httpClient(implicit appContext: AppContext): Client[IO] = appContext.client
}
