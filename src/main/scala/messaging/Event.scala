package messaging

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

final case class Event(event_name: String, value: EventFormat)

object Event {
  implicit val codec: Codec[Event] = deriveCodec[Event]
}
