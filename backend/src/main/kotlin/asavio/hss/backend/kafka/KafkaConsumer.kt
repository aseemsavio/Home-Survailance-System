package asavio.hss.backend.kafka

import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.*

/**
 * Creates and returns a Kafka Consumer.
 */
fun <K, V> createKafkaConsumer(properties: () -> Properties): KafkaConsumer<K, V> {
    return KafkaConsumer<K, V>(properties())
}
