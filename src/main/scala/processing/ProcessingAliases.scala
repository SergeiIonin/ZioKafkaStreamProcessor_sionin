package processing

import zio.Has
import zio.blocking.Blocking
import zio.clock.Clock
import zio.kafka.consumer.Consumer
import zio.kafka.producer.Producer
import zio.logging.Logging

object ProcessingAliases {
  type Collecting = Has[Collecting.Service]

  type Cache = Has[Cache.Service]

  type Pipeline = Has[Pipeline.Service]

  type PipelineEnvironment = Clock
    with Blocking
    with Logging
    with Consumer
    with Producer[Any, String, String]
    with Collecting

}
