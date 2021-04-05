package messaging

import io.circe.{Decoder, Encoder, HCursor, Json}

final case class Report(event_name: String, eventValues: Set[EventFormat], eventCalled: Int)

object Report {

  implicit val encoder: Encoder[Report] = (r: Report) => Json.obj(
    "event_name" -> Json.fromString(r.event_name),
    "eventCalled" -> Json.fromInt(r.eventCalled)
  )

  implicit val decoder: Decoder[Report] = (hc: HCursor) => for {
    event_name <- hc.downField("event_name").as[String]
    eventCalled <- hc.downField("eventCalled").as[Int]
  } yield {
    Report(event_name, Set.empty, eventCalled)
  }
}
