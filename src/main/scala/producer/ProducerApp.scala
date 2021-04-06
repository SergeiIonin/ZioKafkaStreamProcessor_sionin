package producer

import io.circe.syntax.EncoderOps
import org.apache.kafka.clients.producer.ProducerRecord
import zio._
import zio.blocking._
import zio.kafka.producer._
import zio.kafka.serde._
import zio.logging._
import zio.logging.slf4j._
import zio.stream._
import messaging.{Event, Report}

object ProducerApp extends App {

  override def run(args: List[String]) =
    program.provideSomeLayer[Any with Blocking](appLayer).exitCode

  private lazy val program: ZIO[Any with Blocking with Producer[Any, String, String] with Logging, Throwable, Unit] =
    ZStream
      .fromIterable(EventGenerator.events_short)
      .map(toProducerRecord)
      .mapM { producerRecord =>
        log.info(s"Producing $producerRecord to Kafka...") *>
          Producer.produce[Any, String, String](producerRecord)
      }
      .runDrain

  private lazy val appLayer: ZLayer[Any, Throwable, Logging with Has[Producer.Service[Any, String, String]]] = {
    val producerSettings = ProducerSettings(List("localhost:9092"))
    val producerLayer    = Producer.make[Any, String, String](producerSettings, Serde.string, Serde.string).toLayer

    val loggingLayer = Slf4jLogger.make { (context, message) =>
      val correlationId = LogAnnotation.CorrelationId.render(
        context.get(LogAnnotation.CorrelationId)
      )
      "[correlation-id = %s] %s".format(correlationId, message)
    }

    loggingLayer ++ producerLayer
  }

  private def toProducerRecord(event: Event): ProducerRecord[String, String] =
    new ProducerRecord("data", "event", event.asJson.toString)
}
