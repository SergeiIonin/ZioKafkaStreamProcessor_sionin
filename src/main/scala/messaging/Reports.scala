package messaging

import io.circe.generic.semiauto._
import io.circe.Codec


object Reports {
  type Reports = List[Report]
  implicit val codec: Codec[Reports] = deriveCodec[List[Report]]
}
