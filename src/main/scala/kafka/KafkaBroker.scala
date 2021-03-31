package kafka

import net.manub.embeddedkafka.{EmbeddedK, EmbeddedKafka, EmbeddedKafkaConfig}
import org.slf4j.LoggerFactory


object KafkaBroker extends App with EmbeddedKafka {
  val log = LoggerFactory.getLogger(this.getClass)

  val port = 9092

  log.info(s"ready to startup Kafka server")
  implicit val config: EmbeddedKafkaConfig = EmbeddedKafkaConfig(kafkaPort = port, zooKeeperPort = 5555)

  val embeddedKafkaServer: EmbeddedK = EmbeddedKafka.start()

  createCustomTopic(topic = "data", partitions = 3)
  createCustomTopic(topic = "reports", partitions = 3)
  log.info(s"Kafka running: localhost:$port")

  embeddedKafkaServer.broker.awaitShutdown()
}
