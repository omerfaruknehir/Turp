package app.turp.chat.provider

import app.turp.chat.BuildConfig
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Routes OpenCode Zen and Go models to the wire protocol documented for each model family.
 * This keeps OpenCode first-class instead of pretending the entire gateway is one
 * OpenAI-compatible endpoint.
 */
internal class OpenCodeRoutingProvider(
    private val delegate: ChatProvider,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build(),
) : ChatProvider {
    private val chat = OpenAiCompatibleProvider(client)
    private val anthropic = AnthropicProvider(client)
    private val gemini = GeminiProvider(client)
    private val responses = ResponsesApiTransport(client, includeNativeWebSearch = false)

    override suspend fun stream(request: ChatRequest, emit: suspend (StreamChunk) -> Unit) {
        if (!ModelRequestPolicy.isOpenCode(request.provider)) {
            delegate.stream(request, emit)
            return
        }

        val headers = linkedMapOf<String, String>().apply {
            putAll(request.customHeaders)
            if (keys.none { it.equals("User-Agent", ignoreCase = true) }) {
                put("User-Agent", "Turp-Android/" + BuildConfig.VERSION_NAME)
            }
            if (request.sessionId.isNotBlank() &&
                keys.none { it.equals("x-opencode-session", ignoreCase = true) }
            ) {
                put("x-opencode-session", request.sessionId)
            }
        }
        val routed = request.copy(customHeaders = headers)

        when (ModelRequestPolicy.openCodeTransport(request.provider, request.model)) {
            OpenCodeTransport.RESPONSES -> responses.stream(routed, emit)
            OpenCodeTransport.ANTHROPIC_MESSAGES -> anthropic.stream(routed, emit)
            OpenCodeTransport.GEMINI -> gemini.stream(routed, emit)
            OpenCodeTransport.CHAT_COMPLETIONS -> chat.stream(routed, emit)
        }
    }
}
