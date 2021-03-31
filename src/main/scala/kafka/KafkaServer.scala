package kafka

import kafka.server.{KafkaConfig, KafkaServer}
import org.apache.kafka.clients.producer.KafkaProducer

import java.util.Properties
import scala.collection.JavaConverters._

object KafkaServer extends App {

  val kafkaPort = 9092
  val zooKeeperPort = 5555
  val kafkaProps = Map("kafkaPort" -> kafkaPort, "zooKeeperPort" -> zooKeeperPort).asJava
  val config = new KafkaConfig(kafkaProps)
  new KafkaServer(config).startup()

/*  writeToKafka("quick-start")
    def writeToKafka(topic: String): Unit = {
      val props = new Properties()
      props.put("bootstrap.servers", "localhost:9094")
      props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
      new KafkaProducer[String, String](props)
      //val record = new ProducerRecord[String, String](topic, "key", "value")
      /*      producer.send(record)
            producer.close()*/
    }*/
}
