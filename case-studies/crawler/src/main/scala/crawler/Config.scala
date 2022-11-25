package crawler

import cats.effect.IO
import cats.effect.kernel.Resource
import com.yon.db.MongoConfig
import pureconfig._
import pureconfig.generic.ProductHint
import pureconfig.generic.auto._

case class ServerConfig(host: String, port: Int)

case class KafkaTopic(clientId: String, topic: String)

case class Config(
    server: ServerConfig,
    mongo: MongoConfig,
    kafka: KafkaTopic
)

object Config {

  // Configure camelCase parsing
  implicit def productHint[T]: ProductHint[T] = ProductHint[T](ConfigFieldMapping(CamelCase, CamelCase))

  def resource: Resource.Pure[IO, Config] = Resource.Pure {
    load
  }

  def load: Config = ConfigSource.default.loadOrThrow[Config]
}
