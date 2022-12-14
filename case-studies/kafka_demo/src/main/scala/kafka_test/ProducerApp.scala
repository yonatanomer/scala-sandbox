package kafka_test

import cats.effect.{ExitCode, IO, IOApp}
import com.yon.kafka.MessageProducer
import fs2.Stream
import io.circe.generic.auto._
import kafka_test.CarTrafficDummyData._

import scala.concurrent.duration.DurationInt

object ProducerApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    MessageProducer[CarId, CarSpeed]("car-speed")
      .use(produce)
      .as(ExitCode.Success)
  }

  def produce(producer: MessageProducer[CarId, CarSpeed]): IO[Unit] = {
    Stream
      .emits[IO, (CarId, CarSpeed)](carSpeed)
      .evalMap { case (carId, speed) =>
        producer.send("json-topics", carId, speed)
      }
      .metered(2.seconds)
      .repeat
      .compile
      .drain
    //.covary[IO]
  }
}
