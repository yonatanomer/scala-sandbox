package com.yon.kafka

import cats.effect.{IO, Resource}
import com.yon.kafka.KafkaClientConfig.producerProps
import com.yon.kafka.Serialization.serializer
import io.circe.Encoder
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}

import scala.collection.immutable.Seq
import scala.concurrent.Promise
import scala.concurrent.duration.DurationInt
import scala.jdk.CollectionConverters._

class MessageProducer[K >: Null, V >: Null](kafkaProducer: KafkaProducer[K, V]) {

  def produce(topic: String, records: Seq[(K, V)]): IO[Nothing] = {
    IO(send(topic, records)).foreverM
  }

  private def send(topic: String, records: Seq[(K, V)]): Seq[IO[Unit]] = {
    records.map { case (k, v) =>
      Thread.sleep(1000)

      val p = Promise[Unit]()
      kafkaProducer.send(
        new ProducerRecord[K, V](topic, k, v),
        new Callback {
          override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
            println("produced a record")
            Option(exception).map(p.failure).getOrElse(p.success(()))
          }
        }
      )
      IO.fromFuture(IO(p.future)) *> IO(println(s"produced data to [$topic]")) *> IO.sleep(2.seconds)
    }
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
