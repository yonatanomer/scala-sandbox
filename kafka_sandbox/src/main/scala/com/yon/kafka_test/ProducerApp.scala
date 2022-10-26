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
import CarTrafficDummyData._
import com.yon.kafka_test.Serialization.{CirceJsonSerializer, deserializer, serializer}
import io.circe.generic.auto._

object ProducerApp extends IOApp {
  private val props: Map[String, Object] = Map(
    CLIENT_ID_CONFIG -> "json-topics",
    BOOTSTRAP_SERVERS_CONFIG -> "localhost:9092",
    StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG -> Serdes.stringSerde.getClass
    // in case if needed custom serializer, e.g for protobuf
    //KEY_SERIALIZER_CLASS_CONFIG -> classOf[KafkaAvroSerializer],
    //VALUE_SERIALIZER_CLASS_CONFIG -> classOf[KafkaAvroSerializer],
    //SCHEMA_REGISTRY_URL_CONFIG -> "http://schema-registry:8081"
  )

  override def run(args: List[String]): IO[ExitCode] = {
    Resource
      .make(IO(new KafkaProducer[CarId, CarSpeed](props.asJava, serializer[CarId], serializer[CarSpeed])))(p => {
        IO(p.close())
      })
      .use(produce("car-speed", carSpeed))
      .as(ExitCode.Success)
  }

  def produce[K, V](topic: String, records: Seq[(K, V)])(producer: KafkaProducer[K, V]): IO[Nothing] = {
    IO(send(producer)(topic, records)).foreverM
  }

  private def send[K, V](
      producer: KafkaProducer[K, V]
  )(topic: String, records: Seq[(K, V)]): Seq[IO[Unit]] = {
    records.map { case (k, v) =>
      Thread.sleep(1000)

      val p = Promise[Unit]()
      producer.send(
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

}
