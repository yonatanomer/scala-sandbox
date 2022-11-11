package com.yon.db

import cats.effect.{IO, Resource}
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.{CodecProvider, CodecRegistry}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.{ConnectionString, MongoClient, MongoClientSettings}

import scala.collection.immutable.Seq
import scala.jdk.CollectionConverters._

case class MongoDbClient(client: MongoClient) {}

object MongoDbClient {

  def init(uri: String, codecs: Seq[CodecProvider]): Resource[IO, MongoDbClient] = {
    Resource.make {
      IO(initClient(uri, codecs))
    } { client =>
      IO {
        println("closing mongo")
        client.client.close()
      }
    }
  }

  def initClient(uri: String, codecs: Seq[CodecProvider]): MongoDbClient = {
    new MongoDbClient(MongoClient(clientSettings(uri, codecs)))
  }

  private def codecRegistry(codexProviders: Seq[CodecProvider]): CodecRegistry = fromRegistries(
    fromProviders(codexProviders.asJava),
    DEFAULT_CODEC_REGISTRY
  )

  def clientSettings(uri: String, codexProviders: Seq[CodecProvider]): MongoClientSettings =
    MongoClientSettings
      .builder()
      .applyConnectionString(new ConnectionString(uri))
      .codecRegistry(codecRegistry(codexProviders))
      .build()

}
