package com.yon.kafka_test

import cats.effect.{ExitCode, IO, IOApp, Resource}
import com.yon.kafka_test.CarTrafficDummyData._
import com.yon.kafka_test.KafkaClientConfig.consumerProps
import com.yon.kafka_test.Serialization.deserializer
import io.circe.generic.auto._
import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer}

import java.time.Duration
import scala.collection.immutable.Seq
import scala.jdk.CollectionConverters._

object ConsumerApp extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    Resource
      .make(IO {
        val consumer = new KafkaConsumer[CarId, CarSpeed](
          consumerProps(Some("json-topics-consumer"), "json-topics-consumer", "localhost:9092").asJava,
          deserializer[CarId],
          deserializer[CarSpeed]
        )

        consumer.subscribe(Seq("car-speed").asJava)
        consumer
      })(c => IO(c.close()))
      .use { consumer =>
        val consume: IO[Unit] = for {

          records <- IO(consumer.poll(Duration.ofSeconds(5)).asScala.toSeq)
          recs <- IO {
            println("consumed records:")
            records.map { r =>
              {
                println("record: " + r)
              }
            }
            records
          }
          //_ <- keyValue.traverse { case (k, v) => IO(println(s"[$topic] $k => $v")) }
        } yield ()
        consume.foreverM

      }
      .as(ExitCode.Success)
  }

  def printRecords(records: Seq[ConsumerRecord[CarId, CarSpeed]]) = {
    println("consumed records:")
    records.map { r =>
      {
        println("record: " + r)
      }
    }
  }
}
