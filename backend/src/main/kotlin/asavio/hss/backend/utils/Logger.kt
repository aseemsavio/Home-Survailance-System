package asavio.hss.backend.utils

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import java.time.LocalDateTime

/**
 * Writes an INFO log
 */
fun CoroutineScope.info(text: () -> String) {
    coroutineContext[CoroutineName]
    log(Thread.currentThread().name, coroutineContext[CoroutineName]?.name ?: "default-coroutine", INFO, logEmoji, text)
}

/**
 * Writes an error log
 */
fun CoroutineScope.error(stackTrace: Throwable? = null, text: () -> String) {
    log(Thread.currentThread().name, coroutineContext[CoroutineName]?.name ?: "default-coroutine", ERROR, logEmoji) {
        stackTrace?.let { "${text()}\n${stackTrace.stackTraceToString()}" } ?: text()
    }
}

/**
 * Writes a SUCCESS log
 */
fun CoroutineScope.success(text: () -> String) {
    log(Thread.currentThread().name, coroutineContext[CoroutineName]?.name ?: "default-coroutine", SUCCESS, logEmoji, text)
}

/**
 * Writes a FAILURE log
 */
fun CoroutineScope.failure(text: () -> String) {
    log(Thread.currentThread().name, coroutineContext[CoroutineName]?.name ?: "default-coroutine", FAILURE, logEmoji, text)
}

private fun log(
    thread: String,
    coroutineName: String,
    logType: LogType,
    emoji: Map<LogType, String>,
    text: () -> String
) {
    println("${LocalDateTime.now()} | $thread | $coroutineName | ${emoji[logType]} ${logType::class.simpleName}: ${text()}")
}

sealed class LogType
object INFO : LogType()
object ERROR : LogType()
object WARN : LogType()
object SUCCESS : LogType()
object FAILURE : LogType()

private val logEmoji = mapOf(
    INFO to "👨🏻‍💻",
    ERROR to "🙅🏻‍",
    WARN to "⚠️",
    SUCCESS to "✅",
    FAILURE to "❌️"
)