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
  def insertTask(params: CrawlParams): IO[Either[Exception, CrawlTask]]

  def getTask(id: Long): IO[Option[CrawlTask]]
}

class MongoTasksDao(mongo: MongoDbClient) extends TasksDao {
  import org.mongodb.scala.model.Filters.equal

  private val collection = mongo.client.getDatabase("test").getCollection[CrawlTask]("tasks")

  override def insertTask(params: CrawlParams): IO[Either[Exception, CrawlTask]] = {

    // todo - generate id
    val id = 1
    val task: CrawlTask = CrawlTask(id, params.url, params.pattern, params.depth)

    IO.fromFuture(IO(collection.insertOne(task).toFuture()))
      .map {
        case res: InsertOneResult if res.wasAcknowledged() => Right(task)
        case res                                           => Left(new Exception(s"Failed to insert task: $res"))
      }
  }

  override def getTask(id: Long): IO[Option[CrawlTask]] = {
    IO.fromFuture(IO(collection.find(equal("id", id)).toFuture())).map(_.headOption)
  }

}

object MongoTasksDao {

  val codecs: Seq[CodecProvider] = Seq(classOf[CrawlTask])

  def apply(mongo: MongoDbClient): MongoTasksDao = new MongoTasksDao(mongo)
}
