package messaging

import io.circe.{Decoder, Encoder, HCursor, Json}
import shapeless.Lazy.apply

final case class Report(event_name: String, eventValues: Set[EventFormat], eventCalled: Int)

object Report {

  implicit val encoder: Encoder[Report] = (r: Report) => Json.obj(
    s"${r.event_name}" -> Json.fromInt(r.eventCalled),
  )

  implicit val decoder: Decoder[Report] = (hc: HCursor) => {
    val event_name = hc.keys.map(_.head).getOrElse("")
    hc.downField(event_name).as[Int].map(event_called => Report(event_name, Set.empty, event_called))
  }

}
