package com.yon.kafka

import cats.effect.{IO, Resource}
import com.yon.kafka.KafkaClientConfig.producerProps
import com.yon.kafka.Serialization.serializer
import io.circe.Encoder
import org.apache.kafka.clients.producer.KafkaProducer

import scala.jdk.CollectionConverters._

object ProducerFactory {
  private def makeProducer[K >: Null, V >: Null](clientId: String)(implicit
      keyEncoder: Encoder[K],
      valEncoder: Encoder[V]
  ): Resource[IO, KafkaProducer[K, V]] = {
    Resource
      .make(IO(new KafkaProducer[K, V](producerProps(clientId).asJava, serializer[K], serializer[V])))(p => {
        IO(p.close())
      })
  }

}
