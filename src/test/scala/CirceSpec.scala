import io.circe.parser.decode
import io.circe.syntax._
import io.circe.{Json, Printer}
import messaging._
import org.scalatest.{Matchers, WordSpec}

class CirceSpec extends WordSpec with Matchers with BloomFiltered[EventFormat] with BloomFilterConfig {

  import Report.eventFormatFunnel

  override val bloomFilter = BloomFiltered.initEmptyBloomFilter(Report.dummyExpectedInsertions, Report.dummyFalseProbability)

  "circe" should {
    "properly decode json payload to Event " in {
      val event = Event("event1", 12)
      val parsed = decode[Event]("{\"event_name\":\"event1\",\"value\":12}")
      parsed.fold(e => fail(e), r => r shouldEqual event)
    }
    "properly encode Event to json payload" in {
      val event = Event("event1", 12)
      val expected = "{\"event_name\":\"event1\",\"value\":12}"
      val json: Json = event.asJson
      json.printWith(Printer.noSpaces) shouldEqual expected
    }

    "properly decode json payload to Report" in {
      val report = Report("event1", 3, bloomFilter)
      val parsed = decode[Report]("{\"event1\":3}")
      parsed.fold(e => fail(e), r => r shouldEqual report)
    }

    "properly encode Report to json payload" in {
      val report = Report("event1", 3, bloomFilter)
      val expected = "{\"event1\":3}"
      val json: Json = report.asJson
      json.printWith(Printer.noSpaces) shouldEqual expected
    }
  }

}
