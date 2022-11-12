package kafka_test

import cats.effect.{ExitCode, IO, IOApp}
import com.yon.kafka.MessageProducer
import io.circe.generic.auto._
import kafka_test.CarTrafficDummyData._

object ProducerApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    MessageProducer[CarId, CarSpeed]("json-topics")
      .use(_.produce("car-speed", carSpeed))
      .as(ExitCode.Success)
  }
}
