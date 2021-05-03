import com.google.common.hash.{Funnel, PrimitiveSink}
import messaging.BloomFiltered.initEmptyBloomFilter
import messaging.{BloomFilterConfig, BloomFiltered, EventFormat, Report}
import org.scalatest.{Matchers, WordSpec}

class BloomFilterSpec extends WordSpec with Matchers with BloomFiltered[Long] with BloomFilterConfig {

  import Report.eventFormatFunnel

  override val bloomFilter = initEmptyBloomFilter(100, 0.1)

  "BloomFilter" should {
    "properly accept a set of data and return probabilistic result" in {
      val list0: List[EventFormat] = List(12L, 15L, 18L)
      multipleBloomFilterUpdate(list0)

      list0 foreach { elem =>
        bloomFilter.mightContain(elem) shouldBe true
      }
      val setOut: List[EventFormat] = List(22L, 25L, 28L)
      setOut foreach { elem =>
        bloomFilter.mightContain(elem) shouldBe false
      }
    }
  }

  "2 BloomFilters" should {
    "be equal to each other in case they are initialized and updated in the same way" in {
      val list0: List[EventFormat] = List(1L, 2L, 3L, 3L)
      val bloomFilter0 = initEmptyBloomFilter(100, 0.1)
      list0 foreach { elem =>
        bloomFilter0.put(elem)
      }

      val list1: List[EventFormat] = List(3L, 1L, 2L, 2L)
      val bloomFilter1 = initEmptyBloomFilter(100, 0.1)
      list1 foreach { elem =>
        bloomFilter1.put(elem)
      }

      bloomFilter0 shouldEqual bloomFilter1
    }
  }

}
