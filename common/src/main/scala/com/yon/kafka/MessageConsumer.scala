package com.yon.kafka

import cats.effect.{IO, Resource}
import com.yon.kafka.KafkaClientConfig.consumerProps
import com.yon.kafka.Serialization.deserializer
import io.circe.Decoder
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import org.apache.kafka.clients.consumer.{ConsumerRecord, ConsumerRecords, KafkaConsumer}

import java.time.Duration
import scala.collection.immutable.Seq
import scala.jdk.CollectionConverters._
import io.circe.generic.auto._

class MessageConsumer[K >: Null, V >: Null](topic: String, kafkaConsumer: KafkaConsumer[K, V]) {
  def poll(interval: Duration): IO[scala.Seq[ConsumerRecord[K, V]]] = IO(kafkaConsumer.poll(interval).asScala.toSeq)
  def close(): IO[Unit] = IO(kafkaConsumer.close())
}

object MessageConsumer {
  def apply[K >: Null, V >: Null](consumerId: String, groupId: String, topic: String)(implicit
      keyDecoder: Decoder[K],
      valDecoder: Decoder[V]
  ): Resource[IO, MessageConsumer[K, V]] = {
    Resource.make(IO[MessageConsumer[K, V]] {
      new MessageConsumer(topic, initKafkaConsumer(consumerId, groupId, topic))
    })(_.close())
  }

  private def initKafkaConsumer[K >: Null, V >: Null](
      consumerId: String,
      groupId: String,
      topic: String
  )(implicit keyDecoder: Decoder[K], valDecoder: Decoder[V]): KafkaConsumer[K, V] = {
    val consumer =
      new KafkaConsumer[K, V](consumerProps(Some(consumerId), groupId, "localhost:9092").asJava, deserializer[K], deserializer[V])

    println(s"subscribing consumer $consumerId kafka topic: $topic")
    consumer.subscribe(Seq(topic).asJava)
    consumer
  }
}
