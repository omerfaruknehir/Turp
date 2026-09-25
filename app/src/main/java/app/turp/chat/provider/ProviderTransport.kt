package app.turp.chat.provider

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.job
import okhttp3.Call
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import okio.BufferedSource
import okio.ForwardingSource
import okio.buffer
import kotlin.math.min

internal suspend fun <T> Call.useCancellable(block: suspend (Response) -> T): T {
    val call = this
    val request = call.request()
    val automaticExchangeId = DeveloperHttpTraceStore.automaticExchangeId(request)
    var traceError: String? = null
    val cancellation = currentCoroutineContext().job.invokeOnCompletion { cause ->
        if (cause is CancellationException) call.cancel()
    }
    return try {
        val response = try {
            execute()
        } catch (error: Throwable) {
            traceError = error.message
            throw error
        }
        val tracedResponse = if (automaticExchangeId != null) {
            DeveloperHttpTraceStore.responseStarted(automaticExchangeId, response)
            response.body?.let { body ->
                response.newBuilder()
                    .body(DeveloperTraceResponseBody(automaticExchangeId, body))
                    .build()
            } ?: response
        } else {
            response
        }
        try {
            block(tracedResponse)
        } catch (error: Throwable) {
            traceError = error.message
            throw error
        } finally {
            tracedResponse.close()
        }
    } finally {
        if (automaticExchangeId != null) {
            DeveloperHttpTraceStore.complete(automaticExchangeId, traceError)
            DeveloperHttpTraceStore.releaseAutomaticRequest(request)
        }
        cancellation.dispose()
    }
}

private class DeveloperTraceResponseBody(
    private val exchangeId: String,
    private val delegate: ResponseBody,
) : ResponseBody() {
    private val capture = Buffer()
    private var truncated = false
    private var flushed = false
    private val tracedSource: BufferedSource by lazy {
        object : ForwardingSource(delegate.source()) {
            override fun read(sink: Buffer, byteCount: Long): Long {
                val offset = sink.size
                val read = super.read(sink, byteCount)
                if (read > 0L) {
                    val remaining = (MAX_DEVELOPER_RESPONSE_CAPTURE_BYTES - capture.size).coerceAtLeast(0L)
                    val keep = minOf(read, remaining)
                    if (keep > 0L) sink.copyTo(capture, offset, keep)
                    if (keep < read) truncated = true
                }
                if (read == -1L) flushCapture()
                return read
            }

            override fun close() {
                try {
                    flushCapture()
                } finally {
                    super.close()
                }
            }
        }.buffer()
    }

    override fun contentType(): MediaType? = delegate.contentType()
    override fun contentLength(): Long = delegate.contentLength()
    override fun source(): BufferedSource = tracedSource

    private fun flushCapture() {
        if (flushed) return
        flushed = true
        if (capture.size > 0L) DeveloperHttpTraceStore.appendResponse(exchangeId, capture.readUtf8())
        if (truncated) DeveloperHttpTraceStore.markResponseTruncated(exchangeId)
    }

    private companion object {
        const val MAX_DEVELOPER_RESPONSE_CAPTURE_BYTES = 512L * 1024L
    }
}

internal fun ResponseBody.readErrorSnippet(limit: Long = 8_192): String {
    val source = source()
    source.request(limit)
    return source.buffer.readUtf8(min(source.buffer.size, limit))
}
