package kafka_test

import io.circe.parser.decode
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}
import org.apache.kafka.streams.scala.serialization.Serdes

/** Import this to implicitly create circe JSON serde for any case class
  */
object Serialization {

  implicit def serde[A >: Null: Decoder: Encoder]: Serde[A] = {
    val serializer = (order: A) => order.asJson.noSpaces.getBytes
    val deserializer = (data: Array[Byte]) => {
      decode[A](new String(data)) match {
        case Right(order) => Some(order)
        case Left(err) =>
          println(s"failed to parse message: $err")
          Option.empty
      }
    }
    Serdes.fromFn[A](serializer, deserializer)
  }

  class CirceJsonSerializer[A >: Null: Encoder] extends Serializer[A] {
    override def serialize(topic: String, data: A): Array[Byte] = data.asJson.noSpaces.getBytes
  }

  class CirceJsonDeserializer[A >: Null: Decoder] extends Deserializer[A] {
    override def deserialize(topic: String, data: Array[Byte]): A =
      decode[A](new String(data)) match {
        case Right(obj) => obj
        case Left(err)  => throw new RuntimeException(s"failed to serialize: $err")
      }
  }

  def serializer[A >: Null: Encoder] = new CirceJsonSerializer[A]
  def deserializer[A >: Null: Decoder] = new CirceJsonDeserializer[A]
}
