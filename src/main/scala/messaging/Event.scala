package messaging

import io.circe.Codec
import io.circe.generic.semiauto.deriveCodec

final case class Event(event_name: String, value: Long)
// todo maybe some int code for event will be good for performance

object Event {
  implicit val codec: Codec[Event] = deriveCodec[Event]
}
