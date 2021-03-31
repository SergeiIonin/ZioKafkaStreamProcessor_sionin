import messaging.{Event, Report}
import org.scalatest.{Matchers, WordSpec}
import processing.{AnalyzerError, Cache, Collecting}
import processing.ProcessingAliases.{Cache, Collecting}
import zio.{Exit, ULayer, URIO, ZIO}
import zio.test.Assertion._
import zio.test._
import zio.test.mock.Expectation._
import zio.test.mock._
import zio.Runtime

/*class CacheSpec extends DefaultRunnableSpec {

  def spec: ZSpec[Any, Any] = suite("dsvf") (
    testM("cacheState is updated") {
      val cacheLayer: ULayer[Cache] = Cache.live

      val event = Event("event1", 12)
      (0 until 3) foreach { _ =>
        Cache.put(event)
      }
      val cacheState: URIO[Cache, List[Report]] = Cache.getAll()
      assertM(cacheState)(equalTo(List(Report("event1", 3))))
    }
  )
      //assert(Runtime.default.unsafeRun(cacheState.) == List(Report("event1", 3)))

      //eventCollected.map(list => list shouldEqual List(Report("event10", 1)))
      //val eventCollected: ZIO[Collecting, AnalyzerError, List[Report]] = Collecting.collect(event)
      //assertM(cacheState)(AssertionM(List(Report("event1", 3))))
     /* for {
        reports <- cacheState
        _ = reports shouldBe List(Report("event1", 3)) //zio.console.putStrLn(reports.mkString(", "))
      } yield ()*/

     // val r: URIO[Collecting, Exit[AnalyzerError, List[Report]]] = eventCollected.run.fold(f => f, res => res)
}*/

object ExampleSpec extends DefaultRunnableSpec {

  def spec = suite("ExampleSpec")(
    testM("testing an effect using map operator") {
      ZIO.succeed(1 + 1).map(n => assert(n)(equalTo(2)))
    },
    testM("testing an effect using a for comprehension") {
      val y: ZIO[Any, Nothing, TestResult] = for {
        n <- ZIO.succeed(1 + 1)
      } yield assert(n)(equalTo(2))
      y
    },
    testM("test of Cache") {
      val cacheLayer: ULayer[Cache] = Cache.live

      val event = Event("event1", 12)
      (0 until 3) foreach { _ =>
        Cache.put(event)
      }
      val cacheState: ZIO[Any, Nothing, List[Report]] = Cache.getAll()
      val x: ZIO[Cache, Nothing, TestResult] = for {
        reports <- cacheState
      } yield assert(reports)(equalTo(List(Report("event1", 3))))
      //assertM(cacheState)(equalTo(List(Report("event1", 3))))
    }
  )
}
