package producer

import messaging.Event

object EventGenerator {

  lazy val events_short = List(
    Event("event14", 1),
    Event("event14", 2),
    Event("event14", 3),
    Event("event14", 4),
    Event("event14", 5),

    Event("event24", 1),
    Event("event24", 2),
    Event("event24", 3),
    Event("event24", 4),

    Event("event34", 1),
    Event("event34", 2),
    Event("event34", 3)
  )

  //lazy val events = (0 until 100_000).map(i => Event(s"event-$i", i))
}
