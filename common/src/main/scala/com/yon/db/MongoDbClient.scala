package com.yon.db
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.bson.codecs.configuration.{CodecProvider, CodecRegistry}
import org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.{ConnectionString, MongoClient, MongoClientSettings, MongoDatabase}

import scala.jdk.CollectionConverters._

object MongoDbClient {

  System.setProperty("org.mongodb.async.type", "netty")

  def db(dbName: String, client: MongoClient): MongoDatabase = client.getDatabase(dbName)

  def initClient(uri: String, codecProviders: Seq[CodecProvider]): MongoClient = {

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
