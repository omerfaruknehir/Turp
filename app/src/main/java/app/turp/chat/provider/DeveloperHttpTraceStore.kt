package app.turp.chat.provider

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import okhttp3.Request
import okio.Buffer

data class DeveloperHttpTrace(
    val traceId: String,
    val method: String,
    val url: String,
    val headers: List<Pair<String, String>>,
    val body: String,
    val capturedAt: Long,
) {
    fun formatted(): String = buildString {
        append(method).append(' ').appendLine(url)
        if (headers.isNotEmpty()) {
            appendLine()
            appendLine("Headers")
            headers.forEach { (name, value) -> append(name).append(": ").appendLine(value) }
        }
        if (body.isNotBlank()) {
            appendLine()
            appendLine("Body")
            append(body)
        }
    }
}

object DeveloperHttpTraceStore {
    private const val MAX_CAPTURE_BODY_BYTES = 512 * 1024L
    private const val MAX_TRACES = 96
    private val _traces = MutableStateFlow<Map<String, DeveloperHttpTrace>>(emptyMap())
    val traces: StateFlow<Map<String, DeveloperHttpTrace>> = _traces.asStateFlow()

    fun record(traceId: String, request: Request) {
        if (traceId.isBlank()) return
        val trace = runCatching {
            DeveloperHttpTrace(
                traceId = traceId,
                method = request.method,
                url = redactUrl(request.url.toString()),
                headers = request.headers.names().sorted().map { name ->
                    name to if (isSensitiveName(name)) "[REDACTED]" else request.header(name).orEmpty()
                },
                body = captureBody(request),
                capturedAt = System.currentTimeMillis(),
            )
        }.getOrElse { error ->
            DeveloperHttpTrace(
                traceId = traceId,
                method = request.method,
                url = redactUrl(request.url.toString()),
                headers = emptyList(),
                body = "[HTTP request capture failed: ${error::class.java.simpleName}]",
                capturedAt = System.currentTimeMillis(),
            )
        }
        val updated = LinkedHashMap(_traces.value)
        updated[traceId] = trace
        while (updated.size > MAX_TRACES) updated.remove(updated.keys.first())
        _traces.value = updated
    }

    fun clear(traceId: String) {
        if (traceId !in _traces.value) return
        _traces.value = _traces.value - traceId
    }

    internal fun redactBody(raw: String): String {
        if (raw.isBlank()) return raw
        val parsed = runCatching { ProviderJson.parseToJsonElement(raw) }.getOrNull()
        return if (parsed != null) redactJson(parsed).toString() else redactLooseText(raw)
    }

    private fun captureBody(request: Request): String {
        val body = request.body ?: return ""
        val length = runCatching { body.contentLength() }.getOrDefault(-1L)
        if (length > MAX_CAPTURE_BODY_BYTES) {
            return "[body omitted from developer capture: $length bytes > $MAX_CAPTURE_BODY_BYTES byte limit]"
        }
        val buffer = Buffer()
        body.writeTo(buffer)
        if (buffer.size > MAX_CAPTURE_BODY_BYTES) {
            return "[body omitted from developer capture: ${buffer.size} bytes > $MAX_CAPTURE_BODY_BYTES byte limit]"
        }
        return redactBody(buffer.readUtf8())
    }

    private fun redactJson(value: JsonElement): JsonElement = when (value) {
        is JsonObject -> JsonObject(value.mapValues { (key, child) ->
            if (isSensitiveName(key)) JsonPrimitive("[REDACTED]") else redactJson(child)
        })
        is JsonArray -> JsonArray(value.map(::redactJson))
        else -> value
    }

    private fun redactUrl(raw: String): String =
        Regex("""(?i)([?&](?:api[_-]?key|access[_-]?token|token|secret|password|authorization)=)[^&]*""")
            .replace(raw) { match -> match.groupValues[1] + "[REDACTED]" }

    private fun redactLooseText(raw: String): String =
        Regex("""(?i)("(?:api[_-]?key|access[_-]?token|token|secret|password|authorization)"\s*:\s*")[^"]*(")""")
            .replace(raw) { match -> match.groupValues[1] + "[REDACTED]" + match.groupValues[2] }

    private fun isSensitiveName(name: String): Boolean {
        val normalized = name.lowercase()
        return normalized in setOf(
            "authorization",
            "proxy-authorization",
            "cookie",
            "set-cookie",
            "x-api-key",
            "api-key",
            "x-goog-api-key",
            "chatgpt-account-id",
        ) || normalized.contains("password") ||
            normalized.contains("secret") ||
            normalized.contains("token") ||
            normalized.endsWith("_key") ||
            normalized.endsWith("-key")
    }
}
