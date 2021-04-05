import messaging.{Event, Report}
import processing.Cache
import zio.test.Assertion._
import zio.test._

object CacheSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[_root_.zio.test.environment.TestEnvironment, Any] = suite("ExampleSpec")(

    testM("Cache should persist and return list of reports for each event") {
      val event1_1 = Event("event1", 12)
      val event1_2 = Event("event1", 15)
      val event1_3 = Event("event1", 18)
      val event1_4 = Event("event1", 18)

      val event2_1 = Event("event2", 26)
      val event2_2 = Event("event2", 28)
      val event2_3 = Event("event2", 28)

      val event3_1 = Event("event3", 91)
      val event3_2 = Event("event3", 91)
      (for {
        _ <- Cache.put(event1_1)
        _ <- Cache.put(event1_2)
        _ <- Cache.put(event1_3)
        _ <- Cache.put(event1_4)

        _ <- Cache.put(event2_1)
        _ <- Cache.put(event2_2)
        _ <- Cache.put(event2_3)

        _ <- Cache.put(event3_1)
        _ <- Cache.put(event3_2)
        cacheState <- Cache.getAll()
      } yield assert(cacheState)(equalTo(List(Report("event1", Set(12, 15, 18), 3), Report("event2", Set(26, 28), 2),
        Report("event3", Set(91), 1))))
        ).provideLayer(Cache.live)
    },
    testM("Cache should persist and return list of reports for each event w/ accuracy 99.5") {
      val event1_1 = Event("event1", 100_000) // unique
      val event1_2 = Event("event1", 99_901) // not unique
      val event1_3 = Event("event1", 100_099) // not unique
      val event1_4 = Event("event1", 1)

      val event2_1 = Event("event2", 100_000) // unique
      val event2_2 = Event("event2", 99_899) // unique
      val event2_3 = Event("event2", 100_051) // not unique

      (for {
        _ <- Cache.put(event1_1)
        _ <- Cache.put(event1_2)
        _ <- Cache.put(event1_3)
        _ <- Cache.put(event1_4)

        _ <- Cache.put(event2_1)
        _ <- Cache.put(event2_2)
        _ <- Cache.put(event2_3)
        cacheState <- Cache.getAll()
      } yield assert(cacheState)(equalTo(List(Report("event1", Set(100_000, 1), 2), Report("event2", Set(100_000, 99899), 2))))
        ).provideLayer(Cache.live)
    }

  )
}
