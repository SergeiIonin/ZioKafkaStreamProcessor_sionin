package processing

import messaging.{Event, EventFormat, Report}
import processing.ProcessingAliases.Cache
import zio._

object Cache {

  val accuracy = 0.001

  trait Service {
    def get(event_name: String): UIO[Option[Report]]

    def getAll(): UIO[List[Report]]

    def put(event: Event): UIO[Unit]

    def empty(): UIO[Unit]
  }

  def get(event_name: String): URIO[Cache, Option[Report]] =
    ZIO.accessM(_.get.get(event_name))

  def getAll(): URIO[Cache, List[Report]] =
    ZIO.accessM(_.get.getAll())

  def put(event: Event): URIO[Cache, Unit] =
    ZIO.accessM(_.get.put(event))

  def empty(): URIO[Cache, Unit] =
    ZIO.accessM(_.get.empty())

  lazy val live: ULayer[Cache] =
    Ref.make(Map.empty[String, Report]).map(new Live(_)).toLayer

  final class Live(ref: Ref[Map[String, Report]]) extends Service {

    override def get(event_name: String): UIO[Option[Report]] =
      for {
        cache <- ref.get
        result <- ZIO.succeed(cache.get(event_name))
      } yield result

    override def put(event: Event): UIO[Unit] = {
      def updateCache(cache: Map[String, Report]) = {
        val event_name = event.event_name
        val report = cache.get(event_name)
        report.fold(cache.updated(event_name, Report(event_name, Set(event.value), 1)))(
          report => {
            if (isEventUnique(report.eventValues, event.value)) {
              cache.updated(event_name, Report(event_name, report.eventValues + event.value, report.eventCalled + 1))
            } else cache
          }
        )
      }
      def isEventUnique(recordedValues: Set[EventFormat], newEventValue: EventFormat): Boolean = {
        !recordedValues.map(recorded => (recorded.toDouble / newEventValue - 1).abs).exists(_ <= accuracy)
      }
      ref.update(updateCache)
    }

    override def getAll(): UIO[List[Report]] = {
      for {
        cache <- ref.get
        res <- ZIO.succeed(cache.values.toList)
      } yield res
    }

    override def empty(): UIO[Unit] = ref.get.map(_ => ref.update(_ => Map.empty[String, Report]))

  }

}
