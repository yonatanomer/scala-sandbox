package kafka_test

import cats.effect.{ExitCode, IO, IOApp}
import com.yon.kafka.KafkaClientConfig._
import com.yon.kafka.ProducerFactory._
import io.circe.generic.auto._
import kafka_test.CarTrafficDummyData._
import org.apache.kafka.clients.producer.{Callback, KafkaProducer, ProducerRecord, RecordMetadata}

import scala.collection.immutable.Seq
import scala.concurrent.Promise
import scala.concurrent.duration.DurationInt

object ProducerApp extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {
    producer[CarId, CarSpeed]("json-topics")
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
