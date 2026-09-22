package app.turp.chat.provider

import app.turp.chat.data.ProviderEntity
import app.turp.chat.data.ProviderKind
import app.turp.chat.data.ProviderProfile
import app.turp.chat.data.ProviderProtocol
import java.net.URI
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

enum class ProviderEndpointKey(val wireName: String) {
    MODELS("models"),
    CHAT("chat"),
    IMAGES("images"),
    ACCOUNT("account"),
    INFO("info"),
    PROVIDERS("providers"),
    GENERATE("generate"),
}

val ProviderEntity.effectiveProtocol: ProviderProtocol
    get() = if (protocol != ProviderProtocol.AUTO) protocol else when (kind) {
        ProviderKind.OPENAI_COMPATIBLE -> ProviderProtocol.OPENAI_COMPATIBLE
        ProviderKind.OPENAI_OAUTH -> ProviderProtocol.OPENAI_OAUTH
        ProviderKind.ANTHROPIC -> ProviderProtocol.ANTHROPIC
        ProviderKind.GEMINI -> ProviderProtocol.GEMINI
    }

val ProviderEntity.effectiveProfile: ProviderProfile
    get() = if (profile != ProviderProfile.AUTO) profile else inferLegacyProfile(this)

fun ProviderProtocol.legacyKind(): ProviderKind = when (this) {
    ProviderProtocol.AUTO,
    ProviderProtocol.OPENAI_COMPATIBLE,
    ProviderProtocol.OPENCODE_V2 -> ProviderKind.OPENAI_COMPATIBLE
    ProviderProtocol.OPENAI_OAUTH -> ProviderKind.OPENAI_OAUTH
    ProviderProtocol.ANTHROPIC -> ProviderKind.ANTHROPIC
    ProviderProtocol.GEMINI -> ProviderKind.GEMINI
}

fun providerProtocolLabel(value: ProviderProtocol): String = when (value) {
    ProviderProtocol.AUTO -> "Automatic (legacy)"
    ProviderProtocol.OPENAI_COMPATIBLE -> "OpenAI-compatible"
    ProviderProtocol.OPENAI_OAUTH -> "ChatGPT OAuth"
    ProviderProtocol.ANTHROPIC -> "Anthropic Messages"
    ProviderProtocol.GEMINI -> "Google Gemini"
    ProviderProtocol.OPENCODE_V2 -> "OpenCode V2"
}

fun providerProfileLabel(value: ProviderProfile): String = when (value) {
    ProviderProfile.AUTO -> "Automatic (legacy)"
    ProviderProfile.GENERIC -> "Generic"
    ProviderProfile.OPENAI -> "OpenAI"
    ProviderProfile.OPENROUTER -> "OpenRouter"
    ProviderProfile.OPENCODE_V2 -> "OpenCode V2"
    ProviderProfile.OPENCODE_GO -> "OpenCode Go"
    ProviderProfile.OPENCODE_ZEN -> "OpenCode Zen"
    ProviderProfile.DEEPSEEK -> "DeepSeek"
    ProviderProfile.GROQ -> "Groq"
    ProviderProfile.MISTRAL -> "Mistral"
    ProviderProfile.XAI -> "xAI"
    ProviderProfile.QWEN_CLOUD -> "Qwen Cloud"
    ProviderProfile.OLLAMA -> "Ollama / local"
    ProviderProfile.ANTHROPIC -> "Anthropic"
    ProviderProfile.GEMINI -> "Gemini"
    ProviderProfile.OPENAI_OAUTH -> "ChatGPT OAuth"
}

object ProviderEndpointResolver {
    fun resolve(provider: ProviderEntity, key: ProviderEndpointKey): String {
        val overrides = parseEndpointOverrides(provider.endpointOverridesJson)
        val configured = overrides[key.wireName]?.takeIf(String::isNotBlank)
        val value = configured ?: defaultRelative(provider, key)
        require(!value.isNullOrBlank()) {
            "No ${key.wireName} endpoint is configured for ${provider.displayName}"
        }
        return resolveAgainstBase(provider.baseUrl, value)
    }

    internal fun parseEndpointOverrides(raw: String): Map<String, String> {
        if (raw.isBlank()) return emptyMap()
        val root = runCatching { ProviderJson.parseToJsonElement(raw) as? JsonObject }
            .getOrElse { throw IllegalArgumentException("Endpoint overrides must be a JSON object", it) }
            ?: throw IllegalArgumentException("Endpoint overrides must be a JSON object")
        return root.mapNotNull { (key, value) ->
            value.jsonPrimitive.contentOrNull?.trim()?.takeIf(String::isNotBlank)?.let { key to it }
        }.toMap()
    }

    internal fun resolveAgainstBase(baseUrl: String, rawEndpoint: String): String {
        val endpoint = rawEndpoint.trim()
        val absolute = runCatching { URI(endpoint) }.getOrNull()
        if (absolute?.isAbsolute == true) return ProviderEndpointPolicy.validate(endpoint)
        val root = ProviderEndpointPolicy.validate(baseUrl)
        return root.trimEnd('/') + "/" + endpoint.trimStart('/')
    }

    private fun defaultRelative(provider: ProviderEntity, key: ProviderEndpointKey): String? =
        when (provider.effectiveProtocol) {
            ProviderProtocol.OPENAI_COMPATIBLE -> when (key) {
                ProviderEndpointKey.MODELS -> "models"
                ProviderEndpointKey.CHAT -> "chat/completions"
                ProviderEndpointKey.IMAGES -> if (provider.effectiveProfile == ProviderProfile.OPENROUTER) "images" else "images/generations"
                ProviderEndpointKey.ACCOUNT -> if (provider.effectiveProfile == ProviderProfile.OPENROUTER) "key" else null
                else -> null
            }
            ProviderProtocol.ANTHROPIC -> when (key) {
                ProviderEndpointKey.MODELS -> "models"
                ProviderEndpointKey.CHAT -> "messages"
                else -> null
            }
            ProviderProtocol.GEMINI -> when (key) {
                ProviderEndpointKey.MODELS -> "models"
                else -> null
            }
            ProviderProtocol.OPENCODE_V2 -> when (key) {
                ProviderEndpointKey.INFO -> "api/info"
                ProviderEndpointKey.MODELS -> "api/model"
                ProviderEndpointKey.PROVIDERS -> "api/provider"
                ProviderEndpointKey.GENERATE -> "api/experimental/generate"
                else -> null
            }
            ProviderProtocol.OPENAI_OAUTH,
            ProviderProtocol.AUTO -> null
        }
}

private fun inferLegacyProfile(provider: ProviderEntity): ProviderProfile {
    val id = provider.id.lowercase()
    return when {
        ModelRequestPolicy.matchesPresetId(id, "openrouter") || ModelRequestPolicy.isOpenRouterBaseUrl(provider.baseUrl) ->
            ProviderProfile.OPENROUTER
        ModelRequestPolicy.matchesPresetId(id, "opencode-go") || ModelRequestPolicy.isOpenCodeGoBaseUrl(provider.baseUrl) ->
            ProviderProfile.OPENCODE_GO
        ModelRequestPolicy.matchesPresetId(id, "opencode-zen") || ModelRequestPolicy.isOpenCodeZenBaseUrl(provider.baseUrl) ->
            ProviderProfile.OPENCODE_ZEN
        ModelRequestPolicy.matchesPresetId(id, "openai") || ModelRequestPolicy.isOfficialOpenAiBaseUrl(provider.baseUrl) ->
            ProviderProfile.OPENAI
        ModelRequestPolicy.matchesPresetId(id, "deepseek") -> ProviderProfile.DEEPSEEK
        ModelRequestPolicy.matchesPresetId(id, "groq") -> ProviderProfile.GROQ
        ModelRequestPolicy.matchesPresetId(id, "mistral") -> ProviderProfile.MISTRAL
        ModelRequestPolicy.matchesPresetId(id, "xai") -> ProviderProfile.XAI
        ModelRequestPolicy.matchesPresetId(id, "qwen-cloud") || ModelRequestPolicy.isQwenCloudBaseUrl(provider.baseUrl) ->
            ProviderProfile.QWEN_CLOUD
        ModelRequestPolicy.matchesPresetId(id, "ollama") -> ProviderProfile.OLLAMA
        provider.kind == ProviderKind.ANTHROPIC -> ProviderProfile.ANTHROPIC
        provider.kind == ProviderKind.GEMINI -> ProviderProfile.GEMINI
        provider.kind == ProviderKind.OPENAI_OAUTH -> ProviderProfile.OPENAI_OAUTH
        else -> ProviderProfile.GENERIC
    }
}
