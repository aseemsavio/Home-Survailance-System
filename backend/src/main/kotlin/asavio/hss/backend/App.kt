package asavio.hss.backend

import asavio.hss.backend.kafka.kafkaConsumerConfig
import asavio.hss.backend.kafka.createKafkaConsumer
import asavio.hss.backend.kafka.poll
import asavio.hss.backend.utils.coroutineName
import asavio.hss.backend.utils.info
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Entry Point of the Surveillance Backend
 */
suspend fun main() = coroutineScope {

    val consumer =
        createKafkaConsumer<String, ByteArray> {
            kafkaConsumerConfig {
                bootstrapServers = "localhost:9093"
                keyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer"
                valueDeserializer = "org.apache.kafka.common.serialization.ByteArrayDeserializer"
                otherProperties = mapOf(
                    "max.partition.fetch.bytes" to "10000000", /* 10 MB */
                    "fetch.max.bytes" to "10000000", /* 10 MB */
                    "session.timeout.ms" to "46000",
                    "group.id" to "deep-learning-model"
                )
            }
        }

    consumer.poll(listOf("foobar")) {
        coroutineScope {
            launch("processing-coroutine".coroutineName) {
                val value = it.value()
                info { "Consumed image of size: ${value.size}" }
            }
        }
    }
}
/*

./bin/kafka-topics.sh --create --topic foobar --partitions 1 --replication-factor 1 --bootstrap-server localhost:9093
./bin/kafka-console-producer.sh --topic foobar --bootstrap-server localhost:9093
./bin/kafka-console-consumer.sh --topic foobar --from-beginning --bootstrap-server localhost:9093
        */
