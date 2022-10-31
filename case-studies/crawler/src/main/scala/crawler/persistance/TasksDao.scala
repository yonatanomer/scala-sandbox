package crawler.persistance

import cats.effect.IO
import crawler.api.CrawlParams

case class CrawlTask(id: Long, url: String, pattern: String, depth: Option[Int])

trait TasksDao {
  def insertTask(params: CrawlParams): IO[Either[Exception, CrawlTask]]

  def getTask(id: Long): IO[Option[CrawlTask]]
}

object MongoTasksDao extends TasksDao {

  override def insertTask(params: CrawlParams): IO[Either[Exception, CrawlTask]] = {
    throw new NotImplementedError()
  }

  override def getTask(id: Long): IO[Option[CrawlTask]] = {
    throw new NotImplementedError()
  }
}
