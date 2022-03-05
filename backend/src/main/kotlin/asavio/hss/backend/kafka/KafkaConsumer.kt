package asavio.hss.backend.kafka

import asavio.hss.backend.utils.info
import asavio.hss.backend.utils.error
import asavio.hss.backend.utils.failure
import asavio.hss.backend.utils.success
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
        success { "Subscribed to topics: $topics." }
        cleanShutDownHook(this)
        success { "Clean Shut-down hook registered." }
        info { "Started polling topics: $topics." }
        while (true) {
            val records = poll(timeOutDuration)
            for (record in records) {
                processEachRecord(record)
                currentOffset.updateCurrentOffset(record)
            }
        }
    } catch (e: WakeupException) {
        failure { "WakeUpException encountered!" }
        commitSync(currentOffset)
    } catch (e: Exception) {
        error(e) { "Exception occurred." }
    } finally {
        use {
            commitSync(currentOffset)
            success { "Committed current offset: $currentOffset to Kafka manually before shutting down." }
        }
    }
}

/**
 * Registers a shut-down hook to ensure clean shut down.
 * This will trigger a [WakeupException], which will be caught.
 * The finally block will need to manually commit the offset.
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


