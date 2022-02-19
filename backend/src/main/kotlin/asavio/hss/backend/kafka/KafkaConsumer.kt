package asavio.hss.backend.kafka

import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.*

/**
 * Creates and returns a Kafka Consumer.
 */
fun <K, V> createKafkaConsumer(properties: () -> Map<String, String>): KafkaConsumer<K, V> {
    val props = Properties()
    properties().forEach { (k, v) ->
        props[k] = v
    }
    return KafkaConsumer<K, V>(props)
}
