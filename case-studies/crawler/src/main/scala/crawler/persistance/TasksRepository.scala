package crawler.persistance

import cats.effect.IO
import crawler.api.CrawlParams

case class CrawlTask(id: Long, url: String, pattern: String, depth: Option[Int])

object TasksRepository {

  def insertTask(params: CrawlParams): IO[Either[Exception, CrawlTask]] = {
    throw new NotImplementedError()
  }

  def getTask(id: Long): IO[Option[CrawlTask]] = {
    throw new NotImplementedError()
  }

  def getTables(): IO[List[String]] = {
    throw new NotImplementedError()
  }

}
