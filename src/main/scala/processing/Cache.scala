package processing

import messaging.{Event, Report}
import processing.ProcessingAliases.Cache
import zio._

object Cache {

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
      def updateCache(cache: Map[String, Report]): Map[String, Report] = {
        val event_name = event.event_name
        val reportOpt = cache.get(event_name)
        reportOpt.fold(cache.updated(event_name,
          initReport(event_name, event.value)))(
          report => {
            if (report.isNewEventValueUnique(event.value)) {
              cache.updated(event_name, updateReport(report, event.value))
            } else cache
          }
        )
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

    private def initReport(event_name: String, eventValue: Long) = {
      val report = Report(event_name, 1, Report.createEmptyBloomFilter)
      report.updateBloomFilter(eventValue)
      report
    }

    private def updateReport(report: Report, eventValue: Long) = {
      report.updateBloomFilter(eventValue)
      report.copy(eventCalled = report.eventCalled + 1)
    }

  }

}
