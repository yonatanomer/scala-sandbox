package com.yon.kafka

import cats.effect.{IO, Resource}
import com.yon.kafka.KafkaClientConfig.producerProps
import com.yon.kafka.Serialization.serializer
import io.circe.Encoder
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}

import scala.concurrent.Promise
import scala.concurrent.duration.DurationInt
import scala.jdk.CollectionConverters._

class MessageProducer[K >: Null, V >: Null](kafkaProducer: KafkaProducer[K, V]) {

  def send(topic: String, key: K, value: V): IO[Unit] = {
    val p = Promise[Unit]()
    println(s"Sending message to topic $topic")
    kafkaProducer.send(
      new ProducerRecord[K, V](topic, key, value),
      new Callback {
        override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
          println("message sent")
          Option(exception).map(p.failure).getOrElse(p.success(()))
        }
      }
    )
    IO.fromFuture(IO(p.future))
  }

  def close(): IO[Unit] = IO(kafkaProducer.close())
}

object MessageProducer {
  def apply[K >: Null, V >: Null](clientId: String)(implicit
      keyEncoder: Encoder[K],
      valEncoder: Encoder[V]
  ): Resource[IO, MessageProducer[K, V]] = {
    Resource.make(IO {
      val kafkaProducer = new KafkaProducer[K, V](
        producerProps(clientId).asJava,
        serializer[K],
        serializer[V]
      )
      new MessageProducer(kafkaProducer)
    })(_.close())
  }

}
