package kafka
import kafka.server.KafkaServer
import org.apache.kafka.clients.consumer.KafkaConsumer

import java.util.Properties
import org.apache.kafka.clients.producer._

import scala.collection.JavaConverters._

object Kafka extends App {

  val topic = "data"

  createProducer(topic)
  val consumer = createConsumer(topic)
  while(true) {
    consumer.poll(1000)
  }

  def createProducer(topic: String) = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    new KafkaProducer[String, String](props)
    //val record = new ProducerRecord[String, String](topic, "key", "value")
    /*      producer.send(record)
          producer.close()*/
  }

  def createConsumer(topic: String) = {
    val props = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("auto.offset.reset", "latest")
    props.put("group.id", "consumer-group")
    val consumer = new KafkaConsumer[String, String](props)
    consumer.subscribe(List(topic).asJava)
    consumer
    /*while (true) {
      val record = consumer.poll(1000).asScala
      for (data <- record.iterator)
        println(data.value())
    }*/
  }


}
