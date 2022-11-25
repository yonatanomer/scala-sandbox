package crawler.persistance

import cats.effect.IO
import com.yon.db.MongoDbClient
import crawler.web_api.domain.CrawlParams
import crawler.persistance.domain.CrawlTask
import org.bson.codecs.configuration.CodecProvider
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.result.InsertOneResult

import scala.collection.immutable.Seq
import org.mongodb.scala.model.Filters.equal

trait TasksDao {
  def insertTask(params: CrawlParams): IO[Either[Exception, CrawlTask]]

  def getTask(id: Long): IO[Option[CrawlTask]]
}

trait MongoTasksDao extends TasksDao {

  def mongo: MongoDbClient
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
}
