package processing

import zio.blocking.Blocking
import zio.clock.Clock
import zio.console.Console
import zio.duration.durationInt
import zio.kafka.consumer.{Consumer, ConsumerSettings}
import zio.kafka.producer.{Producer, ProducerSettings}
import zio.kafka.serde.Serde
import zio.logging.{LogAnnotation, Logging}
import zio.logging.slf4j.Slf4jLogger
import zio.{ExitCode, URIO, ZLayer}

object ProcessingApp extends zio.App {

  override def run(args: List[String]): URIO[Blocking with Clock with Console, ExitCode] =
    Pipeline.run.provideSomeLayer(appLayer).exitCode

  private lazy val appLayer = {
    val loggingLayer: ZLayer[Any, Nothing, Logging] = Slf4jLogger.make { (context, message) =>
      val correlationId = LogAnnotation.CorrelationId.render(context.get(LogAnnotation.CorrelationId))
      "[correlation-id = %s] %s".format(correlationId, message)
    }
    val collectingLayer = (loggingLayer ++ Cache.live) >>> Collecting.live

    (Clock.live ++ Blocking.live ++ loggingLayer ++ kafkaLayer ++ collectingLayer) >>> Pipeline.live
  }

  private lazy val kafkaLayer = {
    val consumerSettings =
      ConsumerSettings(List("localhost:9092"))
        .withGroupId("group-0")
        .withClientId("client")
        .withCloseTimeout(30.seconds)
        .withPollTimeout(10.millis)
        .withProperty("enable.auto.commit", "false")
        .withProperty("auto.offset.reset", "latest")

    val producerSettings = ProducerSettings(List("localhost:9092"))

    val consumerLayer = Consumer.make(consumerSettings).toLayer
    val producerLayer = Producer.make[Any, String, String](producerSettings, Serde.string, Serde.string).toLayer

    consumerLayer ++ producerLayer
  }
}
