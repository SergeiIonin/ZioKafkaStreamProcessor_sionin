package producer

import messaging.Event

object EventGenerator {

  lazy val events = List(
    Event("event1", 1),
    Event("event1", 1),
    Event("event1", 1),
    Event("event1", 1),
    Event("event1", 1),
    Event("event2", 2),
    Event("event2", 2),
    Event("event2", 2),
    Event("event2", 2),
    Event("event3", 3),
    Event("event3", 3),
    Event("event3", 3),
    Event("event3", 3),
  )
}
