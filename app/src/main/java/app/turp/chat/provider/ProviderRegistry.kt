package app.turp.chat.provider

import app.turp.chat.data.ProviderEntity
import app.turp.chat.data.ProviderKind
import app.turp.chat.data.ProviderProtocol

class ProviderRegistry(oauth: OpenAiOAuthManager) {
    private val openAiCompatible = OpenAiCompatibleProvider()
    private val openAiImages = OpenAiImageStreamingProvider(openAiCompatible)
    private val openAi = AlibabaCloudRequestRoutingProvider(
        NativeWebSearchProvider(AlibabaImageRoutingProvider(openAiImages)),
    )
    private val openAiWithOpenCode = OpenCodeRoutingProvider(openAi)
    private val openAiOAuth = OpenAiOAuthProvider(oauth)
    private val anthropic = NativeWebSearchProvider(AnthropicProvider())
    private val gemini = NativeWebSearchProvider(GeminiProvider())
    private val openCodeV2 = OpenCodeV2Provider()

    fun get(provider: ProviderEntity): ChatProvider = when (provider.effectiveProtocol) {
        ProviderProtocol.OPENAI_COMPATIBLE -> openAiWithOpenCode
        ProviderProtocol.OPENAI_OAUTH -> openAiOAuth
        ProviderProtocol.ANTHROPIC -> anthropic
        ProviderProtocol.GEMINI -> gemini
        ProviderProtocol.OPENCODE_V2 -> openCodeV2
        ProviderProtocol.AUTO -> get(provider.kind)
    }

    fun get(kind: ProviderKind): ChatProvider = when (kind) {
        ProviderKind.OPENAI_COMPATIBLE -> openAiWithOpenCode
        ProviderKind.OPENAI_OAUTH -> openAiOAuth
        ProviderKind.ANTHROPIC -> anthropic
        ProviderKind.GEMINI -> gemini
    }
}
