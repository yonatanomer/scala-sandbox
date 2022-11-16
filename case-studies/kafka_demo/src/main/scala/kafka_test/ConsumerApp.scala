package kafka_test

import cats.effect.{ExitCode, IO, IOApp, Resource}
import io.circe.generic.auto._
import kafka_test.CarTrafficDummyData.{CarId, CarSpeed}
import com.yon.kafka.MessageConsumer
import cats.implicits._

import org.apache.kafka.clients.consumer.{ConsumerRecord, KafkaConsumer}

import java.time.Duration
import scala.collection.immutable.Seq

object ConsumerApp extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    MessageConsumer[CarId, CarSpeed]("car-consumer", "cars-consumer-group", "car-speed")
      .use { consumer =>
        val consume: IO[Unit] = for {
          records <- consumer.poll(Duration.ofSeconds(5))
          recs <- IO {
            println("consumed records:")
            records.map { r =>
              {
                println("record: " + r)
              }
            }
            records
          }
          //.traverse { case (k, v) => IO(println(s"[$topic] $k => $v")) }
        } yield ()
        consume.foreverM

      }
      .as(ExitCode.Success)
  }
}
