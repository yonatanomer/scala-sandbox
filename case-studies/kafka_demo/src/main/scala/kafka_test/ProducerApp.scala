package kafka_test

import cats.effect.{ExitCode, IO, IOApp}
import com.yon.kafka.MessageProducer
import io.circe.generic.auto._
import kafka_test.CarTrafficDummyData._

import scala.collection.immutable.Seq
import scala.concurrent.duration.DurationInt

object ProducerApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    MessageProducer[CarId, CarSpeed]("json-topics")
      .use(x => produce("car-speed", carSpeed)(x))
      .as(ExitCode.Success)
  }
// todo on fixed in terval
  def produce(topic: String, records: Seq[(CarId, CarSpeed)])(producer: MessageProducer[CarId, CarSpeed]): IO[Nothing] = {
    IO {
      records.map { case (k, v) =>
        producer
          .send(topic, k, v) *> IO {
          println(s"produced data to [$topic]")
        }
      }
    }.foreverM
  }

}
