package com.yon.db

import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.Filters
import org.mongodb.scala.bson.codecs.Macros._
import org.bson.codecs.configuration.CodecProvider

import scala.collection.immutable.Seq
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import Model._

/**  just a helper to test the mongo client, set the needed case class and collection name
  */

object Model {
  val collectionName = "sniff"
  case class ExampleDoc(id: Long, something: String)

  val codecs: Seq[CodecProvider] = Seq(classOf[ExampleDoc])
}

object MongoSniffer extends App {

  val mongo = MongoDbClient.initClient("mongodb://test:test@0.0.0.0:27017", codecs)

  val db = mongo.client.getDatabase(collectionName)

  println("creating collection")
  Await.ready(db.createCollection("sniff").toFuture(), 10.seconds)
  val collection: MongoCollection[ExampleDoc] = db.getCollection[ExampleDoc]("tasks").withDocumentClass()

  println("inserting")
  val task = ExampleDoc(1, "something good")
  val ret = Await.result(collection.insertOne(task).toFuture(), 10.seconds)

  println(s"reading")
  val findRes = Await.result(collection.find(Filters.equal("id", 1)).toFuture(), 10.seconds)

  println(s"found $findRes")
}
