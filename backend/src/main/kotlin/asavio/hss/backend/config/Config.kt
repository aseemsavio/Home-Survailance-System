package asavio.hss.backend.config

import kotlin.reflect.full.memberProperties

data class KafkaConsumerConfig(
    val bootstrapServers: String,
    val groupId: String,
    val keyDeserializer: String,
    val valueDeserializer: String
)

fun kafkaConsumerConfig(fn: KafkaConsumerConfigBuilder.() -> Unit) =
    KafkaConsumerConfigBuilder().apply(fn).build().toMap()

class KafkaConsumerConfigBuilder {

    var bootstrapServers: String? = null
    var groupId: String? = null
    var keyDeserializer: String? = null
    var valueDeserializer: String? = null

    fun build(): KafkaConsumerConfig {
        check(bootstrapServers != null)
        check(keyDeserializer != null)
        check(valueDeserializer != null)
        return KafkaConsumerConfig(
            bootstrapServers = bootstrapServers!!,
            groupId = groupId!!,
            keyDeserializer = keyDeserializer!!,
            valueDeserializer = valueDeserializer!!
        )
    }
}

fun KafkaConsumerConfig.toMap(): Map<String, String> {
    val map: MutableMap<String, String> = mutableMapOf()
    this::class.memberProperties.map {
        map[it.name.kafkaKey()!!] = it.getter.call(this).toString()
    }
    return map
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
