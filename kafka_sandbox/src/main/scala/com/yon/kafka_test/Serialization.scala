package com.yon.kafka_test

import io.circe.{Decoder, Encoder}
import org.apache.kafka.common.serialization.Serde
import org.apache.kafka.streams.scala.serialization.Serdes
import io.circe.parser.decode
import io.circe.syntax._

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

}
