package asavio.hss.backend.kafka

import java.util.*
import kotlin.reflect.full.memberProperties

data class KafkaConsumerConfig(
    val bootstrapServers: String,
    val keyDeserializer: String,
    val valueDeserializer: String,
    /* Do NOT rename the following property. */
    val otherProperties: Map<String, String> = emptyMap()
)

fun kafkaConsumerConfig(fn: KafkaConsumerConfigBuilder.() -> Unit) =
    KafkaConsumerConfigBuilder().apply(fn).build().toProperties()

class KafkaConsumerConfigBuilder {

    var bootstrapServers: String? = null
    var keyDeserializer: String? = null
    var valueDeserializer: String? = null
    var otherProperties: Map<String, String> = emptyMap()

    fun build(): KafkaConsumerConfig {
        check(bootstrapServers != null)
        check(keyDeserializer != null)
        check(valueDeserializer != null)
        return KafkaConsumerConfig(
            bootstrapServers = bootstrapServers!!,
            keyDeserializer = keyDeserializer!!,
            valueDeserializer = valueDeserializer!!,
            otherProperties = otherProperties
        )
    }
}

fun KafkaConsumerConfig.toProperties(): Properties {
    val properties = Properties()
    this::class.memberProperties.map {
        when (val key = it.name) {
            "otherProperties" -> {
                val map = it.getter.call(this) as Map<*, *>
                for ((k, v) in map) {
                    properties[k.toString()] = v.toString()
                }
            }
            else -> {
                val value = it.getter.call(this)
                if (value != null) properties[key.kafkaKey()] = value.toString()

            }
        }
    }
    return properties
}

/**
 * Converts camel case into full lower case words separated by a dot.
 * this format is required by Kafka Config Keys.
 */
private fun String.kafkaKey(): String? {
    return replace(
        String.format(
            "%s|%s|%s",
            "(?<=[A-Z])(?=[A-Z][a-z])",
            "(?<=[^A-Z])(?=[A-Z])",
            "(?<=[A-Za-z])(?=[^A-Za-z])"
        ).toRegex(),
        "."
    ).lowercase()
}
