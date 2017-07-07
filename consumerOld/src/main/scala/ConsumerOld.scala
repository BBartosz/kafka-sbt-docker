import java.util.concurrent._
import java.util.{Collections, Properties}

import org.apache.kafka.clients.consumer.{ConsumerConfig, KafkaConsumer}

import scala.collection.JavaConversions._

object ConsumerOld extends App {
  var executor: ExecutorService = null
  val broker = "kafka:9092"
  val groupId = "kafka_old_docker_group"
  val topic = "kafka_docker_topic"

  def shutdown() = {
    if (consumer != null)
      consumer.close();
    if (executor != null)
      executor.shutdown();
  }

  val props = new Properties()
  props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, broker)
  props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId)
  props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true")
  props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000")
  props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000")
  props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
  props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")

  val consumer = new KafkaConsumer[String, String](props)

    consumer.subscribe(Collections.singletonList(topic))

  Executors.newSingleThreadExecutor.execute(new Runnable {
    override def run(): Unit = {
      while (true) {
        val records = consumer.poll(1000)

        for (record <- records) {
          System.out.println("KafkaAPI 0.9.0 consumer: Received message: (" + record.value() + ") at offset " + record.offset())
        }
      }
    }
  })
}