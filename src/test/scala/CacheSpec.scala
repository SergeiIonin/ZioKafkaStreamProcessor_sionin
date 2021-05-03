import com.google.common.hash.{BloomFilter, Funnel, PrimitiveSink}
import messaging.{BloomFilterConfig, BloomFiltered, Event, EventFormat, Report}
import processing.Cache
import zio.test.Assertion._
import zio.test._

object CacheSpec extends DefaultRunnableSpec {

  override def spec: ZSpec[_root_.zio.test.environment.TestEnvironment, Any] = suite("ExampleSpec")(

    testM("Cache should persist and return list of reports for each event") {
      val event1_1 = Event("event1", 12L)
      val event1_2 = Event("event1", 15L)
      val event1_3 = Event("event1", 18L)
      val event1_4 = Event("event1", 18L)

      val event2_1 = Event("event2", 26L)
      val event2_2 = Event("event2", 28L)
      val event2_3 = Event("event2", 28L)

      val event3_1 = Event("event3", 91L)
      val event3_2 = Event("event3", 91L)
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
      } yield assert(cacheState)(equalTo(List(
        Report("event1", 3, BloomFilterHelper().createBloomFilterWithListValues(List(12L, 15L, 18L))),
        Report("event2", 2, BloomFilterHelper().createBloomFilterWithListValues(List(26L, 28L))),
        Report("event3", 1, BloomFilterHelper().createBloomFilterWithListValues(List(91L))))
      ))
    ).provideLayer(Cache.live)
   }

  )
}

case class BloomFilterHelper() extends BloomFiltered[EventFormat] with BloomFilterConfig {
  import Report.eventFormatFunnel
  override val bloomFilter = Report.createEmptyBloomFilter
  def createBloomFilterWithListValues(list: List[EventFormat]): BloomFilter[EventFormat] = {
    multipleBloomFilterUpdate(list)
  }
}
