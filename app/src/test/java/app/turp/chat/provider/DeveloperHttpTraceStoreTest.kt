package app.turp.chat.provider

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DeveloperHttpTraceStoreTest {
    @Test
    fun redactsSensitiveJsonFieldsRecursively() {
        val raw = """{"model":"test","api_key":"secret","nested":{"access_token":"token","safe":"visible"}}"""
        val redacted = DeveloperHttpTraceStore.redactBody(raw)

        assertTrue(redacted.contains("[REDACTED]"))
        assertTrue(redacted.contains("visible"))
        assertTrue(redacted.contains("test"))
        assertFalse(redacted.contains("secret"))
        assertFalse(redacted.contains("\"token\""))
    }

    @Test
    fun capturesFinalRequestAndResponseWhileRedactingSecrets() {
        val traceId = "trace-final-http"
        DeveloperHttpTraceStore.clear(traceId)
        val request = Request.Builder()
            .url("https://example.test/v1/chat?api_key=top-secret&mode=fast")
            .header("Authorization", "Bearer credential")
            .header("X-Safe", "visible")
            .post(
                """{"model":"unit-model","access_token":"body-secret","messages":[{"role":"user","content":"hello"}]}"""
                    .toRequestBody("application/json".toMediaType()),
            )
            .build()

        val exchangeId = DeveloperHttpTraceStore.record(traceId, request)
        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .header("Set-Cookie", "session=response-secret")
            .header("X-Response-Safe", "yes")
            .build()
        DeveloperHttpTraceStore.responseStarted(exchangeId, response)
        DeveloperHttpTraceStore.appendResponse(
            exchangeId,
            """data: {"reasoning_content":"provider reasoning","access_token":"stream-secret"}\n\n""",
        )
        DeveloperHttpTraceStore.complete(exchangeId)

        val trace = DeveloperHttpTraceStore.traces.value[traceId].orEmpty().single()
        assertEquals("POST", trace.method)
        assertTrue(trace.url.contains("/v1/chat"))
        assertTrue(trace.url.contains("mode=fast"))
        assertFalse(trace.url.contains("top-secret"))
        assertTrue(trace.url.contains("[REDACTED]"))
        assertTrue(trace.headers.contains("X-Safe" to "visible"))
        assertTrue(trace.headers.contains("Authorization" to "[REDACTED]"))
        assertTrue(trace.body.contains("unit-model"))
        assertTrue(trace.body.contains("hello"))
        assertFalse(trace.body.contains("body-secret"))
        assertEquals(200, trace.responseStatus)
        assertTrue(trace.responseHeaders.contains("X-Response-Safe" to "yes"))
        assertTrue(trace.responseHeaders.contains("Set-Cookie" to "[REDACTED]"))
        assertTrue(trace.responseBody.contains("provider reasoning"))
        assertFalse(trace.responseBody.contains("stream-secret"))
        assertNotNull(trace.durationMs)
        DeveloperHttpTraceStore.clear(traceId)
    }

    @Test
    fun responseCaptureIsBoundedAndMarksTruncation() {
        val traceId = "trace-bounded"
        DeveloperHttpTraceStore.clear(traceId)
        val request = Request.Builder().url("https://example.test/").get().build()
        val exchangeId = DeveloperHttpTraceStore.record(traceId, request)

        DeveloperHttpTraceStore.appendResponse(exchangeId, "x".repeat(600 * 1024))
        DeveloperHttpTraceStore.complete(exchangeId)

        val trace = DeveloperHttpTraceStore.traces.value[traceId].orEmpty().single()
        assertTrue(trace.responseBodyTruncated)
        assertTrue(trace.responseBody.toByteArray().size <= 512 * 1024)
        assertTrue(trace.formatted().contains("truncated"))
        DeveloperHttpTraceStore.clear(traceId)
    }

    @Test
    fun sudoProvidedToolsAreSerializedEvenWhenCatalogMetadataDisagrees() {
        val openAi = java.io.File("src/main/java/app/turp/chat/provider/OpenAiCompatibleProvider.kt").readText()
        val anthropic = java.io.File("src/main/java/app/turp/chat/provider/AnthropicProvider.kt").readText()
        val gemini = java.io.File("src/main/java/app/turp/chat/provider/GeminiProvider.kt").readText()
        val oauth = java.io.File("src/main/java/app/turp/chat/provider/OpenAiOAuthProvider.kt").readText()

        assertTrue(openAi.contains("if (request.tools.isNotEmpty())"))
        assertFalse(openAi.contains("request.tools.isNotEmpty() && request.model.supportsTools"))
        assertTrue(anthropic.contains("if (request.tools.isNotEmpty())"))
        assertFalse(anthropic.contains("request.tools.isNotEmpty() && request.model.supportsTools"))
        assertTrue(gemini.contains("if (request.tools.isNotEmpty())"))
        assertFalse(gemini.contains("request.tools.isNotEmpty() && request.model.supportsTools"))
        assertFalse(oauth.contains("if (request.model.supportsTools) request.tools.forEach"))
    }
}
