package asavio.hss.backend.kafka

import asavio.hss.backend.utils.info
import asavio.hss.backend.utils.error
import asavio.hss.backend.utils.failure
import asavio.hss.backend.utils.success
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.clients.consumer.OffsetAndMetadata
import org.apache.kafka.common.TopicPartition
import org.apache.kafka.common.errors.TimeoutException
import org.apache.kafka.common.errors.WakeupException
import java.lang.Exception
import java.time.Duration
import java.util.*
import kotlin.collections.HashMap
import kotlin.system.exitProcess

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
) = coroutineScope {
    val currentOffset = HashMap<TopicPartition, OffsetAndMetadata>()
    try {
        ensureKafkaIsAlive(this@poll)
        subscribe(topics)
        success { "Subscribed to topics: $topics." }
        cleanShutDownHook(this@poll)
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
            if (currentOffset.isNotEmpty())
                success { "Committed current offset: $currentOffset to Kafka manually before shutting down." }
        }
    }
}

/**
 * Ensures Kafka is alive and is able to connect to this consumer.
 */
private suspend fun <K, V> ensureKafkaIsAlive(
    kafkaConsumer: KafkaConsumer<K, V>,
    timeOutSeconds: Long = 5
) = coroutineScope {
    try {
        info { "Attempting to establish a connection with Kafka..." }
        kafkaConsumer.listTopics(Duration.ofSeconds(timeOutSeconds))
    } catch (e: TimeoutException) {
        failure { "Could not connect to Kafka. Shutting Down..." }
        exitProcess(1)
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


