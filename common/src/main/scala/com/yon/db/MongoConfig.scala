package com.yon.db

import com.yon.ConfigWrapper

case class MongoConfig(user: String, password: String, port: String) {}

object MongoConfig {
  def apply(config: ConfigWrapper): MongoConfig = {
    MongoConfig(
      config.getString("mongo.user"),
      config.getString("mongo.password"),
      config.getString("mongo.port")
    )
  }
}
