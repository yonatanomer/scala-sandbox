package com.yon.kafka

import cats.effect.{IO, Resource}
import com.yon.kafka.KafkaClientConfig.producerProps
import com.yon.kafka.Serialization.serializer
import io.circe.Encoder
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}

import scala.concurrent.Promise
import scala.jdk.CollectionConverters._

trait MessageProducer[K >: Null, V >: Null] {

  val kafkaProducer: KafkaProducer[K, V]

  def send(topic: String, key: K, value: V): IO[Either[Exception, Unit]] = {
    val p = Promise[Either[Exception, Unit]]()
    println(s"Sending message to topic $topic")
    kafkaProducer.send(
      new ProducerRecord[K, V](topic, key, value),
      new Callback {
        override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
          exception match {
            case null => {
              println(s"Message sent to topic $topic")
              p.success(Right(()))
            }
            case e =>
              println(s"Error sending message to topic $topic: $e")
              p.success(Left(e))
          }
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
      val producer = new KafkaProducer[K, V](
        producerProps(clientId).asJava,
        serializer[K],
        serializer[V]
      )
      new MessageProducer[K, V] {
        override val kafkaProducer: KafkaProducer[K, V] = producer
      }
    })(_.close())
  }

  def kafkaProducer[K >: Null, V >: Null](clientId: String)(implicit
      keyEncoder: Encoder[K],
      valEncoder: Encoder[V]
  ): Resource[IO, KafkaProducer[K, V]] = {
    Resource.make(IO {
      new KafkaProducer[K, V](
        producerProps(clientId).asJava,
        serializer[K],
        serializer[V]
      )
    })(producer => IO(producer.close()))
  }

}
