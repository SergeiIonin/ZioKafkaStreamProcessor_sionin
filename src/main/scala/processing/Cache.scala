package processing

import messaging.{Event, Report}
import processing.ProcessingAliases.Cache
import zio.{UIO, ULayer, URIO, ZIO, Ref}

object Cache {

  trait Service {
    def get(event_name: String): UIO[Option[Report]]
    def getAll(): UIO[List[Report]]
    def put(event: Event): UIO[Unit]
  }

  def get(event_name: String): URIO[Cache, Option[Report]] =
    ZIO.accessM(_.get.get(event_name))

  def getAll(): URIO[Cache, List[Report]] =
    ZIO.accessM(_.get.getAll())

  def put(event: Event): URIO[Cache, Unit] =
    ZIO.accessM(_.get.put(event))

  lazy val live: ULayer[Cache] =
    Ref.make(Map.empty[String, Report]).map(new Live(_)).toLayer

  final class Live(ref: Ref[Map[String, Report]]) extends Service {
    override def get(event_name: String): UIO[Option[Report]] =
      for {
        cache  <- ref.get
        result <- ZIO.succeed(cache.get(event_name))
      } yield result

    override def put(event: Event): UIO[Unit] = {
      for {
        cache <- ref.get
        eventCalled = cache.get(event.event_name).fold(0)(report => report.eventCalled)
      } yield {
        val cacheUpd: Map[String, Report] = cache.updated(event.event_name, Report(event.event_name, eventCalled + 1))
        ref.update(_ => cacheUpd)
        ()
      }
    }

    override def getAll(): UIO[List[Report]] = {
      for {
        cache <- ref.get
        res <- ZIO.succeed(cache.values.toList)
      } yield res
    }
  }
}
