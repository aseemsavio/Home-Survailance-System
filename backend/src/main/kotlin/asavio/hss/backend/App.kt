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
            bootstrapServers = ""
            groupId = ""
            keyDeserializer = ""
            valueDeserializer = ""
        }
    }

}