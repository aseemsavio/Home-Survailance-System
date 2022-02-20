package asavio.hss.backend.kafka

import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.errors.WakeupException
import java.lang.Exception
import java.time.Duration
import java.util.*
import kotlin.collections.HashMap

/**
 * Creates and returns a Kafka Consumer.
 */
fun <K, V> createKafkaConsumer(properties: () -> Properties): KafkaConsumer<K, V> {
    return KafkaConsumer<K, V>(properties())
}

/**
 * Polls for records indefinitely. Commits are automatic.
 */
suspend fun <K, V> KafkaConsumer<K, V>.poll(
    topics: List<String>,
    timeOutDuration: Duration = 1000L.duration,
    cleanShutDownHook: (KafkaConsumer<K, V>) -> Unit = ::cleanShutDownHook,
    processEachRecord: suspend (ConsumerRecord<K, V>) -> Unit
) {
    val currentOffset = HashMap<TopicPartition, OffsetAndMetadata>()
    try {
        subscribe(topics)
        cleanShutDownHook(this)
        while (true) {
            val records = poll(timeOutDuration)
            for (record in records) {
                processEachRecord(record)
                currentOffset.updateCurrentOffset(record)
            }
        }
    } catch (e: WakeupException) {
        /* ignore, we're closing */
    } catch (e: Exception) {
        /* do something */
    } finally {
        use {
            commitSync(currentOffset)
        }
    }
}

/**
 * Registers a shut-down hook to ensure clean shut down.
 */
private fun <K, V> cleanShutDownHook(consumer: KafkaConsumer<K, V>) {
    Runtime.getRuntime().addShutdownHook(object : Thread() {
        override fun run() = runBlocking { consumer.wakeup() }
    })
}

/**
 * Updates the current offset to Kafka.
 * The updated offset is the first record that will be read during the next poll cycle.
 */
private fun <K, V> HashMap<TopicPartition, OffsetAndMetadata>.updateCurrentOffset(record: ConsumerRecord<K, V>) {
    this[TopicPartition(record.topic(), record.partition())] = OffsetAndMetadata(record.offset() + 1, null)
}


