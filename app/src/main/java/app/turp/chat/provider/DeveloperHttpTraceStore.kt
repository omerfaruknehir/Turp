package app.turp.chat.provider

import app.turp.chat.settings.DeveloperPromptTraceStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import okhttp3.Request
import okhttp3.Response
import okio.Buffer
import java.time.Instant
import java.util.IdentityHashMap
import java.util.UUID

data class DeveloperHttpTrace(
    val exchangeId: String,
    val traceId: String,
    val providerId: String,
    val profile: String,
    val protocol: String,
    val method: String,
    val url: String,
    val headers: List<Pair<String, String>>,
    val body: String,
    val requestBodyTruncated: Boolean,
    val capturedAt: Long,
    val responseStatus: Int? = null,
    val responseMessage: String = "",
    val responseHeaders: List<Pair<String, String>> = emptyList(),
    val responseBody: String = "",
    val responseBodyTruncated: Boolean = false,
    val completedAt: Long? = null,
    val durationMs: Long? = null,
    val error: String? = null,
) {
    fun formatted(): String = buildString {
        appendLine("[HTTP REQUEST · REDACTED]")
        append("timestamp: ").appendLine(Instant.ofEpochMilli(capturedAt).toString())
        if (providerId.isNotBlank()) append("provider: ").appendLine(providerId)
        if (profile.isNotBlank()) append("profile: ").appendLine(profile)
        if (protocol.isNotBlank()) append("protocol: ").appendLine(protocol)
        append(method).append(' ').appendLine(url)
        if (headers.isNotEmpty()) {
            appendLine()
            appendLine("Headers")
            headers.forEach { (name, value) -> append(name).append(": ").appendLine(value) }
        }
        if (body.isNotBlank()) {
            appendLine()
            appendLine("Body")
            appendLine(body)
        }
        if (requestBodyTruncated) appendLine("[request body truncated by developer capture limit]")

        appendLine()
        appendLine("[HTTP RESPONSE · REDACTED]")
        responseStatus?.let { status ->
            append("status: ").append(status)
            if (responseMessage.isNotBlank()) append(' ').append(responseMessage)
            appendLine()
        } ?: appendLine("status: pending")
        if (responseHeaders.isNotEmpty()) {
            appendLine("Headers")
            responseHeaders.forEach { (name, value) -> append(name).append(": ").appendLine(value) }
        }
        if (responseBody.isNotBlank()) {
            appendLine()
            appendLine("Body / SSE")
            appendLine(responseBody)
        }
        if (responseBodyTruncated) appendLine("[response body/SSE truncated by developer capture limit]")
        durationMs?.let { append("duration_ms: ").appendLine(it.toString()) }
        error?.takeIf(String::isNotBlank)?.let { append("error: ").appendLine(it) }
    }.trimEnd()
}

object DeveloperHttpTraceStore {
    private const val MAX_CAPTURE_BODY_BYTES = 512 * 1024L
    private const val MAX_TRACE_GROUPS = 96
    private const val MAX_EXCHANGES_PER_TRACE = 16
    private val lock = Any()
    private val exchangeToTrace = LinkedHashMap<String, String>()
    private val automaticRequests = IdentityHashMap<Request, String>()
    private val _traces = MutableStateFlow<Map<String, List<DeveloperHttpTrace>>>(emptyMap())
    val traces: StateFlow<Map<String, List<DeveloperHttpTrace>>> = _traces.asStateFlow()

    fun record(request: ChatRequest, httpRequest: Request, protocol: String): String {
        if (request.developerPromptTraceEnabled) {
            DeveloperPromptTraceStore.recordProviderContext(request, protocol)
        }
        val exchangeId = begin(
            traceId = request.developerTraceId,
            providerId = request.provider.id,
            profile = request.provider.effectiveProfile.name,
            protocol = protocol,
            request = httpRequest,
        )
        if (exchangeId.isNotBlank()) synchronized(lock) {
            automaticRequests[httpRequest] = exchangeId
        }
        return exchangeId
    }

    /** Compatibility overload for focused unit tests and any diagnostic caller without a ChatRequest. */
    fun record(traceId: String, request: Request): String {
        val exchangeId = begin(traceId, "", "", "", request)
        if (exchangeId.isNotBlank()) synchronized(lock) { automaticRequests[request] = exchangeId }
        return exchangeId
    }

    internal fun automaticExchangeId(request: Request): String? = synchronized(lock) { automaticRequests[request] }

    internal fun releaseAutomaticRequest(request: Request) {
        synchronized(lock) { automaticRequests.remove(request) }
    }

    private fun begin(
        traceId: String,
        providerId: String,
        profile: String,
        protocol: String,
        request: Request,
    ): String {
        if (traceId.isBlank()) return ""
        val exchangeId = "$traceId:${UUID.randomUUID()}"
        val trace = runCatching {
            val capturedBody = captureBody(request)
            DeveloperHttpTrace(
                exchangeId = exchangeId,
                traceId = traceId,
                providerId = providerId,
                profile = profile,
                protocol = protocol,
                method = request.method,
                url = redactUrl(request.url.toString()),
                headers = redactHeaders(request.headers.names().sorted().map { it to request.header(it).orEmpty() }),
                body = capturedBody.first,
                requestBodyTruncated = capturedBody.second,
                capturedAt = System.currentTimeMillis(),
            )
        }.getOrElse { captureError ->
            DeveloperHttpTrace(
                exchangeId = exchangeId,
                traceId = traceId,
                providerId = providerId,
                profile = profile,
                protocol = protocol,
                method = request.method,
                url = redactUrl(request.url.toString()),
                headers = emptyList(),
                body = "[HTTP request capture failed: ${captureError::class.java.simpleName}]",
                requestBodyTruncated = false,
                capturedAt = System.currentTimeMillis(),
            )
        }
        synchronized(lock) {
            val updated = LinkedHashMap(_traces.value)
            val group = updated[traceId].orEmpty().toMutableList()
            group += trace
            while (group.size > MAX_EXCHANGES_PER_TRACE) {
                exchangeToTrace.remove(group.removeAt(0).exchangeId)
            }
            updated[traceId] = group
            exchangeToTrace[exchangeId] = traceId
            while (updated.size > MAX_TRACE_GROUPS) {
                val oldestKey = updated.keys.first()
                updated.remove(oldestKey).orEmpty().forEach { exchangeToTrace.remove(it.exchangeId) }
            }
            _traces.value = updated
        }
        return exchangeId
    }

    fun responseStarted(exchangeId: String, response: Response) {
        if (exchangeId.isBlank()) return
        update(exchangeId) { trace ->
            trace.copy(
                responseStatus = response.code,
                responseMessage = response.message,
                responseHeaders = redactHeaders(
                    response.headers.names().sorted().map { it to response.header(it).orEmpty() },
                ),
            )
        }
    }

    fun appendResponse(exchangeId: String, raw: String) {
        if (exchangeId.isBlank() || raw.isEmpty()) return
        val safe = redactTransportText(raw)
        update(exchangeId) { trace ->
            if (trace.responseBodyTruncated) return@update trace
            val currentBytes = trace.responseBody.toByteArray(Charsets.UTF_8).size.toLong()
            val remaining = (MAX_CAPTURE_BODY_BYTES - currentBytes).coerceAtLeast(0L).toInt()
            if (remaining == 0) return@update trace.copy(responseBodyTruncated = true)
            val bytes = safe.toByteArray(Charsets.UTF_8)
            if (bytes.size <= remaining) {
                trace.copy(responseBody = trace.responseBody + safe)
            } else {
                val prefix = String(bytes.copyOfRange(0, remaining), Charsets.UTF_8)
                trace.copy(
                    responseBody = trace.responseBody + prefix,
                    responseBodyTruncated = true,
                )
            }
        }
    }

    fun markResponseTruncated(exchangeId: String) {
        if (exchangeId.isBlank()) return
        update(exchangeId) { it.copy(responseBodyTruncated = true) }
    }

    fun complete(exchangeId: String, error: String? = null) {
        if (exchangeId.isBlank()) return
        val now = System.currentTimeMillis()
        update(exchangeId) { trace ->
            trace.copy(
                completedAt = now,
                durationMs = (now - trace.capturedAt).coerceAtLeast(0L),
                error = error?.let(::redactLooseText),
            )
        }
    }

    fun clear(traceId: String) {
        synchronized(lock) {
            val removed = _traces.value[traceId].orEmpty()
            if (removed.isEmpty()) return
            removed.forEach { exchangeToTrace.remove(it.exchangeId) }
            _traces.value = _traces.value - traceId
        }
    }

    internal fun redactBody(raw: String): String {
        if (raw.isBlank()) return raw
        val parsed = runCatching { ProviderJson.parseToJsonElement(raw) }.getOrNull()
        return if (parsed != null) redactJson(parsed).toString() else redactLooseText(raw)
    }

    internal fun redactHeaders(headers: List<Pair<String, String>>): List<Pair<String, String>> =
        headers.map { (name, value) -> name to if (isSensitiveName(name)) "[REDACTED]" else value }

    private fun update(exchangeId: String, transform: (DeveloperHttpTrace) -> DeveloperHttpTrace) {
        synchronized(lock) {
            val traceId = exchangeToTrace[exchangeId] ?: return
            val current = _traces.value[traceId].orEmpty()
            val index = current.indexOfFirst { it.exchangeId == exchangeId }
            if (index < 0) return
            val group = current.toMutableList()
            group[index] = transform(group[index])
            _traces.value = LinkedHashMap(_traces.value).apply { put(traceId, group) }
        }
    }

    private fun captureBody(request: Request): Pair<String, Boolean> {
        val body = request.body ?: return "" to false
        val length = runCatching { body.contentLength() }.getOrDefault(-1L)
        if (length > MAX_CAPTURE_BODY_BYTES) {
            return "[body omitted: $length bytes exceeds $MAX_CAPTURE_BODY_BYTES byte developer capture limit]" to true
        }
        val buffer = Buffer()
        body.writeTo(buffer)
        if (buffer.size > MAX_CAPTURE_BODY_BYTES) {
            return "[body omitted: ${buffer.size} bytes exceeds $MAX_CAPTURE_BODY_BYTES byte developer capture limit]" to true
        }
        return redactBody(buffer.readUtf8()) to false
    }

    private fun redactTransportText(raw: String): String {
        if (raw.startsWith("data:")) {
            val prefixEnd = raw.indexOf(':') + 1
            val prefix = raw.substring(0, prefixEnd)
            val remainder = raw.substring(prefixEnd)
            val leading = remainder.takeWhile(Char::isWhitespace)
            val payload = remainder.drop(leading.length).trimEnd('\r', '\n')
            val suffix = remainder.substring(leading.length + payload.length)
            if (payload.isNotBlank() && payload != "[DONE]") {
                return prefix + leading + redactBody(payload) + suffix
            }
        }
        return redactBody(raw)
    }

    private fun redactJson(value: JsonElement): JsonElement = when (value) {
        is JsonObject -> JsonObject(value.mapValues { (key, child) ->
            if (isSensitiveName(key)) JsonPrimitive("[REDACTED]") else redactJson(child)
        })
        is JsonArray -> JsonArray(value.map(::redactJson))
        else -> value
    }

    internal fun redactUrl(raw: String): String =
        Regex("""([?&])([^=&#]+)=([^&#]*)""").replace(raw) { match ->
            val encodedKey = match.groupValues[2]
            val decodedKey = runCatching {
                java.net.URLDecoder.decode(encodedKey, Charsets.UTF_8.name())
            }.getOrDefault(encodedKey)
            if (isSensitiveName(decodedKey)) {
                match.groupValues[1] + encodedKey + "=[REDACTED]"
            } else {
                match.value
            }
        }

    private fun redactLooseText(raw: String): String = raw
        .let {
            Regex("""(?i)(\"(?:api[_-]?key|access[_-]?token|token|secret|password|authorization)\"\s*:\s*\")[^\"]*(\")""")
                .replace(it) { match -> match.groupValues[1] + "[REDACTED]" + match.groupValues[2] }
        }
        .let {
            Regex("""(?i)(\bBearer\s+)[A-Za-z0-9._~+/=-]+""")
                .replace(it) { match -> match.groupValues[1] + "[REDACTED]" }
        }

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
            "apikey",
            "xapikey",
            "xgoogapikey",
        ) || normalized.contains("password") ||
            normalized.contains("secret") ||
            normalized.contains("token") ||
            normalized.endsWith("_key") ||
            normalized.endsWith("-key") ||
            name.endsWith("Key")
    }
}
