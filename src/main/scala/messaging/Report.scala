package messaging

import com.google.common.hash.{BloomFilter, Funnel, PrimitiveSink}
import io.circe.{Decoder, Encoder, HCursor, Json}

final case class Report(event_name: String, eventCalled: Int, bloomFilter: BloomFilter[EventFormat]) extends BloomFiltered[EventFormat]
    with BloomFilterConfig {
  private val eventValues: BloomFilter[EventFormat] = bloomFilter
  def isNewEventValueUnique(eventValue: EventFormat) = {
    !eventValues.mightContain(eventValue)
  }
}

object Report extends BloomFilterConfig {

  implicit val eventFormatFunnel = new Funnel[EventFormat] {
    override def funnel(from: EventFormat, into: PrimitiveSink): Unit = into.putLong(from)
  }

  def createEmptyBloomFilter: BloomFilter[Long] = BloomFiltered.initEmptyBloomFilter(expectedInsertions, falseProbability)

  implicit val encoder: Encoder[Report] = (r: Report) => Json.obj(
  s"${r.event_name}" -> Json.fromInt(r.eventCalled),
  )
  // the following 3 vals are stubs since we don't recover bloomFilter from json
  val dummyExpectedInsertions = 10
  val dummyFalseProbability = 0.9
  val dummyBloomFilter = BloomFiltered.initEmptyBloomFilter(dummyExpectedInsertions, dummyFalseProbability)

  implicit val decoder: Decoder[Report] = (hc: HCursor) => {
    val event_name = hc.keys.map(_.head).getOrElse("")
    hc.downField(event_name).as[Int].map(event_called => Report(event_name, event_called, dummyBloomFilter))
  }

}
