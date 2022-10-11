package com.yon.kafka_test

import cats.effect.{ExitCode, IO, IOApp, Resource}
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}
import org.apache.kafka.clients.producer.ProducerConfig.{
  BOOTSTRAP_SERVERS_CONFIG,
  CLIENT_ID_CONFIG,
  KEY_SERIALIZER_CLASS_CONFIG,
  VALUE_SERIALIZER_CLASS_CONFIG
}
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.scala.serialization.Serdes

import scala.collection.immutable.Seq
import scala.concurrent.Promise
import scala.concurrent.duration.DurationInt
import scala.jdk.CollectionConverters._

class ProducerApp extends IOApp {

  type key = Int
  type value = String

  private val props: Map[String, Object] = Map(
    CLIENT_ID_CONFIG -> "text-filter-producer",
    BOOTSTRAP_SERVERS_CONFIG -> "kafka:9092",
    StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG -> Serdes.stringSerde.getClass
    // in case if needed custom serializer, e.g for protobuf
    //KEY_SERIALIZER_CLASS_CONFIG -> classOf[KafkaAvroSerializer],
    //VALUE_SERIALIZER_CLASS_CONFIG -> classOf[KafkaAvroSerializer],
    //SCHEMA_REGISTRY_URL_CONFIG -> "http://schema-registry:8081"
  )

  override def run(args: List[String]): IO[ExitCode] = {
    Resource
      .make(IO(new KafkaProducer[key, value](props.asJava)))(p => {
        IO(p.close())
      })
      .use(produce)
      .as(ExitCode.Success)
  }

  def produce(producer: KafkaProducer[key, value]): IO[Either[Exception, Unit]] = {
    IO(Left(new RuntimeException("not implemented yet")))
  }

  private def send[K, V](
      producer: KafkaProducer[K, V]
  )(topic: String, records: Seq[(K, V)]): Seq[IO[Unit]] = {
    records.map { case (k, v) =>
      val p = Promise[Unit]()
      producer.send(
        new ProducerRecord[K, V](topic, k, v),
        new Callback {
          override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit =
            Option(exception).map(p.failure).getOrElse(p.success(()))
        }
      )
      IO.fromFuture(IO(p.future)) *> IO(println(s"produced data to [$topic]")) *> IO.sleep(2.seconds)
    }
  }
}
