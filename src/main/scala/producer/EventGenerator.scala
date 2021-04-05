package producer

import messaging.Event

object EventGenerator {

  lazy val events_short = List(
    Event("event1", 1),
    Event("event1", 2),
    Event("event1", 3),
    Event("event1", 4),
    Event("event1", 5),

    Event("event2", 1),
    Event("event2", 2),
    Event("event2", 3),
    Event("event2", 4),

    Event("event3", 1),
    Event("event3", 2),
    Event("event3", 3)
  )

  lazy val events = (0 until 100_000).map(i => Event(s"event-$i", i))
}
