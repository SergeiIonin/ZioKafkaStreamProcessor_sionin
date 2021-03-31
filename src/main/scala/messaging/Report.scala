package messaging

import io.circe.Codec
import io.circe.generic.semiauto._

final case class Report(event_name: String, eventCalled: Int)

object Report {
  implicit val codec: Codec[Report] = deriveCodec[Report]
}
