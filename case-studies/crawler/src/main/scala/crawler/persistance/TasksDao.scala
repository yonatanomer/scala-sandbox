package crawler.persistance

import cats.data.EitherT
import cats.effect.IO
import com.yon.db.MongoDbClient
import crawler.api.CrawlParams
import org.bson.codecs.configuration.CodecProvider
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.result.InsertOneResult

import scala.collection.immutable.Seq

case class CrawlTask(id: Long, url: String, pattern: String, depth: Option[Int])

trait TasksDao {
  def insertTask(params: CrawlParams): EitherT[IO, Exception, CrawlTask]

  def getTask(id: Long): IO[Option[CrawlTask]]
}

class MongoTasksDao(mongo: MongoDbClient) extends TasksDao {
  import org.mongodb.scala.model.Filters.equal

  private val collection = mongo.client.getDatabase("test").getCollection[CrawlTask]("tasks")

  override def insertTask(params: CrawlParams): EitherT[IO, Exception, CrawlTask] = {

    // todo - generate id
    val id = 1
    val task: CrawlTask = CrawlTask(id, params.url, params.pattern, params.depth)
    EitherT[IO, Exception, CrawlTask](
      IO.fromFuture(IO(collection.insertOne(task).toFuture()))
        .map(mapInsertResult(task))
    )
  }

  override def getTask(id: Long): IO[Option[CrawlTask]] = {
    IO.fromFuture(IO(collection.find(equal("id", id)).toFuture())).map(_.headOption)
  }

  private def mapInsertResult(task: CrawlTask)(res: InsertOneResult): Either[Exception, CrawlTask] = {
    res match {
      case res: InsertOneResult if res.wasAcknowledged() => Right(task)
      case res                                           => Left(new Exception(s"Failed to insert task: $res"))
    }
  }

}

object MongoTasksDao {

  val codecs: Seq[CodecProvider] = Seq(classOf[CrawlTask])

  def apply(mongo: MongoDbClient): MongoTasksDao = new MongoTasksDao(mongo)
}
