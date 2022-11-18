package com.yon
import cats.effect.IO
import cats.effect.kernel.Resource
import com.typesafe.config.{Config, ConfigFactory, ConfigParseOptions}

trait Settings {
  def validated: IO[Config]
}

class ConfigWrapper(config: Config) {
  def getString(key: String): String = config.getString(key)
  def getInt(key: String): Int = config.getInt(key)
  def getBoolean(key: String): Boolean = config.getBoolean(key)
  def getConfig(key: String): ConfigWrapper = new ConfigWrapper(config.getConfig(key))
}

/** parse config, fail fast with exception */
object ConfigWrapper {
  def apply(path: String): ConfigWrapper =
    new ConfigWrapper(parseConfig(path))

  def make(path: String): Resource[IO, ConfigWrapper] =
    Resource.Pure[IO, ConfigWrapper] {
      new ConfigWrapper(parseConfig(path))
    }

  private def parseConfig(path: String) = ConfigFactory
    .parseResources(path, ConfigParseOptions.defaults().setAllowMissing(false))
}
