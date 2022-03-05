package asavio.hss.backend

import asavio.hss.backend.kafka.kafkaConsumerConfig
import asavio.hss.backend.kafka.createKafkaConsumer
import asavio.hss.backend.kafka.poll
import kotlinx.coroutines.runBlocking

/**
 * Entry Point of the Surveillance Backend
 */
fun main() = runBlocking {

    val consumer =
        createKafkaConsumer<String, String> {
            kafkaConsumerConfig {
                bootstrapServers = "localhost:9093"
                keyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer"
                valueDeserializer = "org.apache.kafka.common.serialization.StringDeserializer"
                otherProperties = mapOf(
                    "session.timeout.ms" to "46000",
                    "group.id" to "deep-learning-model"
                )
            }
        }

    consumer.poll(listOf("foobar")) {
        val value = it.value()
        println("Consumed: $value")
    }
}
/*

./bin/kafka-topics.sh --create --topic foobar --partitions 1 --replication-factor 1 --bootstrap-server localhost:9093
./bin/kafka-console-producer.sh --topic foobar --bootstrap-server localhost:9093
./bin/kafka-console-consumer.sh --topic foobar --from-beginning --bootstrap-server localhost:9093
        j*/
