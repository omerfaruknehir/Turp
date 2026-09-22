package app.turp.chat.provider

import app.turp.chat.data.MessageRole
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class OpenCodeV2Provider(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build(),
) : ChatProvider {
    override suspend fun stream(request: ChatRequest, emit: suspend (StreamChunk) -> Unit) =
        withContext(Dispatchers.IO) {
            require(!request.model.supportsImageGeneration) {
                "OpenCode V2 text generation does not expose image generation through this Turp transport."
            }
            val endpoint = ProviderEndpointResolver.resolve(request.provider, ProviderEndpointKey.GENERATE)
            val prompt = buildPrompt(request)
            val modelRef = modelRef(request.model.modelId)
            val body = buildJsonObject {
                put("prompt", JsonPrimitive(prompt))
                if (modelRef != null) {
                    put("model", buildJsonObject {
                        put("id", JsonPrimitive(modelRef.second))
                        put("providerID", JsonPrimitive(modelRef.first))
                    })
                }
            }
            val builder = Request.Builder()
                .url(endpoint)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .post(body.toString().toRequestBody("application/json".toMediaType()))
            if (request.apiKey.isNotBlank()) builder.header("Authorization", "Bearer ${request.apiKey}")
            request.customHeaders.forEach(builder::header)

            client.newCall(builder.build()).useCancellable { response ->
                if (!response.isSuccessful) {
                    val error = response.body?.readErrorSnippet().orEmpty()
                    throw ProviderHttpException(
                        response.code,
                        "OpenCode V2 generation failed (${response.code}): " +
                            error.ifBlank { response.message },
                    )
                }
                val raw = response.body?.string()
                    ?: throw ProviderProtocolException("OpenCode V2 returned an empty response")
                val root = runCatching { ProviderJson.parseToJsonElement(raw).jsonObject }
                    .getOrElse { throw ProviderProtocolException("OpenCode V2 returned invalid JSON", it) }
                val text = root.obj("data")?.string("text")
                    ?: throw ProviderProtocolException("OpenCode V2 response did not contain data.text")
                emit(StreamChunk(text = text, finishReason = "stop"))
            }
        }

    internal fun buildPrompt(request: ChatRequest): String = buildString {
        request.messages.forEach { message ->
            val role = when (message.role) {
                MessageRole.SYSTEM -> "SYSTEM"
                MessageRole.USER -> "USER"
                MessageRole.ASSISTANT -> "ASSISTANT"
                MessageRole.TOOL -> "TOOL"
            }
            append(role).append(": ")
            append(message.content)
            if (message.reasoning.isNotBlank()) {
                append("\n[reasoning context]\n").append(message.reasoning)
            }
            message.nativeToolResults.forEach { result ->
                append("\n[tool ").append(result.name).append("] ").append(result.output)
            }
            message.attachments.forEach { attachment ->
                append("\n[attachment: ").append(attachment.displayName).append("]")
                attachment.extractedText?.takeIf(String::isNotBlank)?.let {
                    append("\n").append(it.take(MAX_ATTACHMENT_TEXT))
                }
            }
            append("\n\n")
        }
        append("ASSISTANT:")
    }

    internal fun modelRef(rawModelId: String): Pair<String, String>? {
        val normalized = rawModelId.trim()
        val slash = normalized.indexOf('/')
        if (slash <= 0 || slash == normalized.lastIndex) return null
        return normalized.substring(0, slash) to normalized.substring(slash + 1)
    }

    private companion object {
        const val MAX_ATTACHMENT_TEXT = 16_000
    }
}
