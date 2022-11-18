package crawler

import cats.effect.{ExitCode, IO, IOApp}
import com.yon.db.MongoDbClient
import com.yon.db.MongoDbClient.{clientSettings, codecRegistry}
import crawler.persistance.{CrawlTask, MongoTasksDao}
import org.bson.codecs.configuration.CodecProvider
import org.mongodb.scala.model.Filters
import org.mongodb.scala.result.InsertOneResult
import org.mongodb.scala.{ConnectionString, MongoClient, MongoClientSettings, MongoCollection}

import scala.collection.immutable.Seq
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt

//object TestMongo extends App {
//  val mongo = MongoDbClient.initClient("mongodb://test:test@0.0.0.0:27017", MongoTasksDao.codecs)
//  val fut = mongo.client.listDatabaseNames().toFuture()
//  println("waiting for fut")
//  val db = mongo.client.getDatabase("test")
//
//  println("creating collection")
//  Await.ready(db.createCollection("tasks").toFuture(), 10.seconds)
//
//  println("collection created")
//  val collection: MongoCollection[CrawlTask] = db.getCollection[CrawlTask]("tasks").withDocumentClass()
//
//  println("inserting")
//  val task = CrawlTask(1, "test-url", "test-pattern", Some(5))
//
//  val ret = Await.result(collection.insertOne(task).toFuture(), 10.seconds)
//  println(s"inserted $ret")
//
//  val findRes = Await.result(collection.find(Filters.equal("id", 1)).toFuture(), 10.seconds)
//  println(s"found $findRes")
//
//}
object TestMongo extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    MongoDbClient
      .make("mongodb://localhost:27017", MongoTasksDao.codecs)
      .use { mongo =>
        IO.fromFuture(IO(mongo.client.listDatabaseNames().toFuture()))
          .flatMap { dbs =>
            IO {
              println(s"DATABASES: $dbs")
            }
          }
      }
      .as(ExitCode.Success)

  }
}
