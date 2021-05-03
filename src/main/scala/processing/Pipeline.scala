package processing

import io.circe.parser.decode
import io.circe.syntax._
import messaging.Report._
import messaging.{Event, Report}
import org.apache.kafka.clients.producer.ProducerRecord
import processing.ProcessingAliases.{Pipeline, PipelineEnvironment}
import zio.kafka.consumer._
import zio.kafka.producer._
import zio.kafka.serde.Serde
import zio.logging.log
import zio.{IO, ZIO, ZLayer}

object Pipeline {

  trait Service {
    def run(): IO[Throwable, Unit]
  }

  def run: ZIO[Pipeline, Throwable, Unit] =
    ZIO.accessM(_.get.run())

  lazy val live: ZLayer[PipelineEnvironment, Nothing, Pipeline] =
    ZLayer.fromFunction { env =>
      () => (log.info("Starting processing pipeline") *>
        Consumer
          .subscribeAnd(Subscription.topics("data"))
          .plainStream(Serde.string, Serde.string)
          .mapM { committableRecord =>
            val parsed = decode[Event](committableRecord.value)

            parsed match {
              case Right(event) =>
                Collecting
                  .collect(event)
                  .map(toProducerRecord)
                  .flatMap(Producer.produce[Any, String, String](_))
                  .as(committableRecord)
              case Left(error) =>
                (log.info(s"Deserialization error $error")
                  *> ZIO.succeed(committableRecord))
            }
          }
          .map(_.offset) // todo try map(_.record)
          .aggregateAsync(Consumer.offsetBatches)
          .mapM(_.commit)
          .runDrain)
        .provide(env)
    }

  private def toProducerRecord(reports: List[Report]): ProducerRecord[String, String] =
    new ProducerRecord("reports", "report", reports.map(_.asJson).mkString(",\n"))
}
