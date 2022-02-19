package asavio.hss.backend

import asavio.hss.backend.kafka.kafkaConsumerConfig
import asavio.hss.backend.kafka.createKafkaConsumer
import kotlinx.coroutines.runBlocking

/**
 * Entry Point of the Surveillance Backend
 */
fun main() = runBlocking {

    val consumer = createKafkaConsumer<String, String> {
        kafkaConsumerConfig {
            bootstrapServers = "host1,host2,host3"
            keyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer"
            valueDeserializer = "org.apache.kafka.common.serialization.StringDeserializer"
            otherProperties = mapOf(
                "session.timeout.ms" to "46000",
                "group.id" to "deep-learning-model"
            )
        }
    }
}