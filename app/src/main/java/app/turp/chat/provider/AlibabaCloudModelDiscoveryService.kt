package app.turp.chat.provider

import app.turp.chat.data.ProviderKind

/**
 * Applies current Model Studio corrections after normal discovery. Alibaba's
 * OpenAI-compatible /models endpoint is intentionally sparse, so provider discovery
 * alone cannot be treated as a complete capability contract.
 */
class AlibabaCloudModelDiscoveryService(
    private val delegate: ModelDiscoveryService,
) {
    constructor(oauth: OpenAiOAuthManager?) : this(ModelDiscoveryService(oauth))

    suspend fun discover(
        kind: ProviderKind,
        rawBaseUrl: String,
        apiKey: String,
        customHeadersJson: String,
        providerId: String? = null,
    ): List<DiscoveredModel> {
        val discovered = delegate.discover(
            kind = kind,
            rawBaseUrl = rawBaseUrl,
            apiKey = apiKey,
            customHeadersJson = customHeadersJson,
            providerId = providerId,
        )
        val isAlibaba = kind == ProviderKind.OPENAI_COMPATIBLE &&
            ModelRequestPolicy.isQwenCloudBaseUrl(rawBaseUrl)
        if (isAlibaba) return discovered.map(AlibabaCloudModelPolicy::correct)

        val isOpenCode = kind == ProviderKind.OPENAI_COMPATIBLE &&
            (ModelRequestPolicy.isOpenCodeGoBaseUrl(rawBaseUrl) ||
                ModelRequestPolicy.isOpenCodeZenBaseUrl(rawBaseUrl))
        return if (isOpenCode) {
            discovered.map { model ->
                ModelRequestPolicy.enrichOpenCodeModel(providerId, rawBaseUrl, model)
            }
        } else {
            discovered
        }
    }
}
