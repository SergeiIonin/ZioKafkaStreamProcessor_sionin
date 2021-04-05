import io.circe.{Json, Printer}
import messaging.{Event, Report}
import org.scalatest.{Matchers, WordSpec}
import io.circe.parser.decode
import io.circe.syntax._

class CirceSpec extends WordSpec with Matchers {

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
      val report = Report("event1", Set(1, 2, 3), 3)
      val parsed = decode[Report]("{\"event_name\":\"event1\",\"eventCalled\":3}")
      val expected = report.copy(eventValues = Set.empty)
      parsed.fold(e => fail(e), r => r shouldEqual expected)
    }

    "properly encode Report to json payload" in {
      val report = Report("event1", Set(1, 2, 3), 3)
      val expected = "{\"event_name\":\"event1\",\"eventCalled\":3}"
      val json: Json = report.asJson
      json.printWith(Printer.noSpaces) shouldEqual expected
    }
  }
}
