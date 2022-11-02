package com.yon.db

import cats.effect.{IO, Resource}
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.{CodecProvider, CodecRegistry}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.{ConnectionString, MongoClient, MongoClientSettings}

import scala.collection.immutable.Seq
import scala.jdk.CollectionConverters._

class MongoDbClient(uri: String, codecProviders: Seq[CodecProvider]) {
  def resource: Resource[IO, MongoClient] = Resource.make(IO(initClient()))(client =>
    IO {
      // todo verify this
      println("closing mongo")
      client.close()
    }
  )

  def initClient(): MongoClient = {
    MongoClient(clientSettings(uri, codecRegistry(codecProviders)))
  }

  private def codecRegistry(codexProviders: Seq[CodecProvider]): CodecRegistry = fromRegistries(
    fromProviders(codexProviders.asJava),
    DEFAULT_CODEC_REGISTRY
  )

  private def clientSettings(uri: String, codecRegistry: CodecRegistry): MongoClientSettings =
    MongoClientSettings
      .builder()
      .applyConnectionString(new ConnectionString(uri))
      .codecRegistry(codecRegistry)
      .build()

}

object MongoDbClient {
  def apply[F[_]](uri: String, codecs: Seq[CodecProvider]): MongoDbClient = new MongoDbClient(uri, codecs)
}
