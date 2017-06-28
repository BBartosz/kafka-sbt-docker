import java.util.concurrent.TimeUnit
import java.util.{Date, Properties}

import monix.execution.Scheduler.{global => scheduler}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

object Producer extends App {
  val topic = "kafka_docker_topic"
  val groupId = "kafka_docker_group"
  val broker = "kafka:9092"

  val props = new Properties()
  props.put("bootstrap.servers", broker)
  props.put("group.id", groupId)
  props.put("client.id", "ScalaProducerExample")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)

  scheduler.scheduleWithFixedDelay(
    3, 10000, TimeUnit.MILLISECONDS,
    new Runnable {
      def run(): Unit = {
        val runtime = new Date().getTime()
        val msg = "Message sent at: " + runtime
        val data = new ProducerRecord[String, String](topic, msg)
        //sync
        producer.send(data)
        println(msg)
        //async
        //producer.send(data, (m,e) => {})
      }
    })

  Thread.sleep(10000000)
  System.out.println("Stopping producer")
  producer.close()
}