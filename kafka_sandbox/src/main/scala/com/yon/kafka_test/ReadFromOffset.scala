package com.yon.kafka_test

import cats.effect.{ExitCode, IO, IOApp, Resource}
import com.yon.kafka_test.CarTrafficDummyData._
import com.yon.kafka_test.KafkaClientConfig.kafkaProps
import com.yon.kafka_test.Serialization.deserializer
import io.circe.generic.auto._
import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer}
import org.apache.kafka.clients.producer.ProducerConfig.{BOOTSTRAP_SERVERS_CONFIG, CLIENT_ID_CONFIG}
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.streams.StreamsConfig
import org.apache.kafka.streams.scala.serialization.Serdes

import java.time.Duration
import java.util
import scala.collection.immutable.Seq
import scala.concurrent.Promise
import scala.concurrent.duration.DurationInt
import scala.jdk.CollectionConverters._

/**  read messages from for a given queue name + offset
  *  reset the offset after reading
  */
object ReadFromOffset extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    Resource
      .make(IO {
        val consumer = new KafkaConsumer[CarId, CarSpeed](
          kafkaProps(None, "json-topics-consumer", "localhost:9092").asJava,
          deserializer[CarId],
          deserializer[CarSpeed]
        )

        readFromOffset(0L, 10, consumer)

        consumer
      })(c => IO(c.close()))
      .use { consumer =>
        // use consumer if needed
        IO()
      }
      .as(ExitCode.Success)
  }

  def readFromOffset(fromOffset: Long, messageCount: Long, consumer: KafkaConsumer[CarId, CarSpeed]) = {

    val partitionToReadFrom = new TopicPartition("car-speed", 0)
    consumer.assign(util.Arrays.asList(partitionToReadFrom));

    consumer.seek(partitionToReadFrom, fromOffset)

    import java.time.Duration
    var count = messageCount
    while (count > 0) {
      val records = consumer.poll(Duration.ofMillis(100))
      import scala.collection.JavaConversions._
      for (record <- records) {
        println("record: " + record)
        count = count - 1
      }
    }

  }

  def printRecords(records: Seq[ConsumerRecord[CarId, CarSpeed]]) = {

    println("consumed records:")
    records.map { r =>
      {
        println("record: " + r)
      }
    }
  }

  def produce[K, V](topic: String, records: Seq[(K, V)])(producer: KafkaProducer[K, V]): IO[Nothing] = {
    IO(send(producer)(topic, records)).foreverM
  }

  private def send[K, V](
      producer: KafkaProducer[K, V]
  )(topic: String, records: Seq[(K, V)]): Seq[IO[Unit]] = {
    records.map { case (k, v) =>
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
