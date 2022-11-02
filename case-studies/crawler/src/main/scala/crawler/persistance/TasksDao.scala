package crawler.persistance

import cats.data.EitherT
import cats.effect.IO
import crawler.api.CrawlParams
import org.bson.codecs.configuration.CodecProvider

import scala.collection.immutable.Seq
import org.mongodb.scala.bson.codecs.Macros._

case class CrawlTask(id: Long, url: String, pattern: String, depth: Option[Int])

trait TasksDao {
  def insertTask(params: CrawlParams): EitherT[IO, Exception, CrawlTask]

  def getTask(id: Long): IO[Option[CrawlTask]]
}

object MongoTasksDao extends TasksDao {

  val codecs: Seq[CodecProvider] = Seq(classOf[CrawlTask])

  override def insertTask(params: CrawlParams): EitherT[IO, Exception, CrawlTask] = {
    EitherT[IO, Exception, CrawlTask](IO(Left(new RuntimeException("insert task not implemented yet"))))
  }

  override def getTask(id: Long): IO[Option[CrawlTask]] = {
    IO.raiseError(new RuntimeException("getTask not implemented yet"))
  }
}
