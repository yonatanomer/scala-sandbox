import com.dimafeng.testcontainers.{ForAllTestContainer, MongoDBContainer}
import com.yon.db.MongoDbClient
import org.bson.codecs.configuration.CodecProvider
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.model.Filters
import org.mongodb.scala.result.InsertOneResult
import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AsyncWordSpecLike

import scala.collection.immutable.Seq
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class MongoDbTest extends AsyncWordSpecLike with Matchers with ForAllTestContainer {

  case class CrawlTask(id: Long, url: String, pattern: String, depth: Option[Int])
  val codecs: Seq[CodecProvider] = Seq(classOf[CrawlTask])

  val database = "test"
  val tasksCollection = "tasks"

  val container: MongoDBContainer = MongoDBContainer()
  private var mongoClient: MongoClient = _

  private def db: MongoDatabase = mongoClient.getDatabase(database)
  private def collection: MongoCollection[CrawlTask] =
    db.getCollection[CrawlTask](tasksCollection).withDocumentClass()

  override def afterStart(): Unit = {
    mongoClient = MongoDbClient.initClient(container.replicaSetUrl, codecs).client

    Await.ready(db.createCollection(tasksCollection).toFuture(), 10.seconds)
    val collections = Await.result(db.listCollectionNames().toFuture(), 10.seconds)
    println(s"created collection $collections")
  }

  "should insert and fetch task by task id" in {
    val task = CrawlTask(1, "test-url", "test-pattern", Some(5))

    val insertRes: InsertOneResult = Await.result(collection.insertOne(task).toFuture(), 10.seconds)
    insertRes.wasAcknowledged() shouldBe true

    val findRes = Await.result(collection.find(Filters.equal("id", 1)).toFuture(), 10.seconds)
    findRes shouldBe List(task)

    1 shouldBe 1
  }

  override def beforeStop(): Unit = {
    println("shutting down Mongo")
    db.drop()
    mongoClient.close()
    super.beforeStop()
  }

}
