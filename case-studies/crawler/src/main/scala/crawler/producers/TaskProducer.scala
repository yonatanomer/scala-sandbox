package crawler.producers

import cats.data.EitherT
import cats.effect.IO
import crawler.persistance.CrawlTask

trait TaskProducer {
  def send(task: CrawlTask): EitherT[IO, Exception, Unit]
}

object KafkaTaskProducer extends TaskProducer {

  override def send(task: CrawlTask): EitherT[IO, Exception, Unit] = {
    EitherT[IO, Exception, Unit](IO(Left(new RuntimeException("send task not implemented yet"))))
  }
}
