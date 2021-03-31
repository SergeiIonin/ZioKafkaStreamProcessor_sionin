package processing

import messaging.{Event, Report}
import processing.ProcessingAliases.{Cache, Collecting}
import zio.logging.{Logging, log}
import zio.{IO, ZIO, ZLayer}

object Collecting {

  trait Service {
    def collect(event: Event): IO[AnalyzerError, List[Report]]
  }

  def collect(event: Event): ZIO[Collecting, AnalyzerError, List[Report]] =
    ZIO.accessM(_.get.collect(event))

  lazy val live: ZLayer[
    Logging with Cache,
    Nothing,
    Collecting
  ] = ZLayer.fromFunction { env =>
    (event: Event) => (for {
      _       <- log.info(s"Processing the event for ${event.event_name}")
      _       <- Cache.put(event)
      reports <- Cache.getAll()
    } yield reports).provide(env)
  }

}
