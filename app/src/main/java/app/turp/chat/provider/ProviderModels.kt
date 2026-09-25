package app.turp.chat.provider

import app.turp.chat.data.AttachmentEntity
import app.turp.chat.data.MessageRole
import app.turp.chat.data.ModelEntity
import app.turp.chat.data.ProviderEntity
import app.turp.chat.data.ThinkingEffort
import app.turp.chat.settings.WebSearchEngine
import app.turp.chat.settings.WebSearchRoute

data class InputMessage(
    val role: MessageRole,
    val content: String,
    val reasoning: String = "",
    val toolTraceJson: String = "[]",
    val attachments: List<AttachmentEntity> = emptyList(),
    val nativeToolCalls: List<NativeToolCall> = emptyList(),
    val nativeToolResults: List<NativeToolResult> = emptyList(),
    /** Provider-specific assistant content blocks/parts which must be echoed unchanged during a tool loop. */
    val nativeProviderPayloadJson: String = "",
    val generatedImages: List<GeneratedImageOutput> = emptyList(),
)

data class ChatRequest(
    val provider: ProviderEntity,
    val model: ModelEntity,
    val apiKey: String,
    val messages: List<InputMessage>,
    val maxOutputTokens: Int,
    val thinkingEnabled: Boolean,
    val thinkingEffort: ThinkingEffort = ThinkingEffort.MEDIUM,
    val continuation: Boolean = false,
    /** Stable Turp chat/workspace identity for providers that support request session affinity. */
    val sessionId: String = "",
    val customHeaders: Map<String, String> = emptyMap(),
    val webSearchRoute: WebSearchRoute = WebSearchRoute.AUTO,
    val webSearchEngine: WebSearchEngine = WebSearchEngine.DUCKDUCKGO,
    val webSearchMaxResults: Int = 8,
    val tools: List<NativeToolDefinition> = emptyList(),
    /** Names recognized only by the protocol firewall; these are never serialized as callable tools. */
    val toolProtocolNames: Set<String> = emptySet(),
    /** Developer-only in-memory HTTP trace key. Blank disables HTTP request/response capture. */
    val developerTraceId: String = "",
    /** Enables provider-boundary effective system-context tracing without enabling HTTP capture. */
    val developerPromptTraceEnabled: Boolean = false,
    /** Resolved developer-editable provider-stage system instructions. Null uses transport defaults. */
    val deepSeekToolGuardPrompt: String? = null,
    val deepSeekToolCorrectionPrompt: String? = null,
    val toolDisabledProtocolCorrectionPrompt: String? = null,
)


data class GeneratedImageOutput(
    val bytes: ByteArray,
    val mimeType: String = "image/png",
    val displayName: String = "generated-image.png",
    val description: String? = null,
)

data class StreamChunk(
    val text: String = "",
    val reasoning: String = "",
    val inputTokens: Long? = null,
    val outputTokens: Long? = null,
    val cachedInputTokens: Long? = null,
    val finishReason: String? = null,
    val toolCallProgress: List<NativeToolCallProgress> = emptyList(),
    val toolCalls: List<NativeToolCall> = emptyList(),
    val nativeProviderPayloadJson: String = "",
    /**
     * Internal provider signal: discard output emitted by the current HTTP attempt before
     * transparently retrying the same model turn.
     */
    val resetCurrentAttempt: Boolean = false,
    /** Final image outputs. These are persisted as normal assistant attachments. */
    val generatedImages: List<GeneratedImageOutput> = emptyList(),
    /**
     * Transient provider-rendered preview for an image request. This must never be
     * persisted as an attachment; later previews replace it until generatedImages arrives.
     */
    val generatedImagePreview: GeneratedImageOutput? = null,
    val generatedImagePreviewIndex: Int? = null,
    val generatedImagePreviewCount: Int? = null,
)

interface ChatProvider {
    suspend fun stream(request: ChatRequest, emit: suspend (StreamChunk) -> Unit)
}

class ProviderHttpException(val status: Int, message: String) : Exception(message)
class ProviderProtocolException(message: String, cause: Throwable? = null) : Exception(message, cause)
