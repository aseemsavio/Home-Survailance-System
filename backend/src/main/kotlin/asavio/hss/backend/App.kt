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
        createKafkaConsumer<String, ByteArray> {
            kafkaConsumerConfig {
                bootstrapServers = "host1,host2,host3"
                keyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer"
                valueDeserializer = "org.apache.kafka.common.serialization.ByteArrayDeserializer"
                otherProperties = mapOf(
                    "session.timeout.ms" to "46000",
                    "group.id" to "deep-learning-model"
                )
            }
        }

    consumer.poll(listOf("topic-name")) {
        val value = it.value()
    }
}