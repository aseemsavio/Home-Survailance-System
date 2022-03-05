package asavio.hss.backend.utils

import java.time.LocalDateTime

/**
 * Writes an INFO log
 */
fun info(text: () -> String) {
    log(INFO, logEmoji, text)
}

/**
 * Writes an error log
 */
fun logError(stackTrace: Throwable? = null, text: () -> String) {
    log(ERROR, logEmoji) {
        stackTrace?.let { "${text()}\n${stackTrace.stackTraceToString()}" } ?: "${text()}"
    }
}

/**
 * Writes a SUCCESS log
 */
fun logSuccess(text: () -> String) {
    log(SUCCESS, logEmoji, text)
}

/**
 * Writes a FAILURE log
 */
fun logFailure(text: () -> String) {
    log(FAILURE, logEmoji, text)
}

private fun log(logType: LogType, emoji: Map<LogType, String>, text: () -> String) {
    println("${LocalDateTime.now()} ${emoji[logType]} ${logType::class.simpleName}: ${text()}")
}

sealed class LogType
object INFO : LogType()
object ERROR : LogType()
object WARN : LogType()
object SUCCESS : LogType()
object FAILURE : LogType()

val logEmoji = mapOf(
    INFO to "👨🏻‍💻",
    ERROR to "🙅🏻‍",
    WARN to "⚠️",
    SUCCESS to "✅",
    FAILURE to "❌️"
)