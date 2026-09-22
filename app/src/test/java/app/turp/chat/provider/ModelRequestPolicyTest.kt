package app.turp.chat.provider

import app.turp.chat.data.ModelEntity
import app.turp.chat.data.ProviderEntity
import app.turp.chat.data.ProviderKind
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelRequestPolicyTest {
    private val official = ProviderEntity(
        id = "openai",
        displayName = "OpenAI API",
        kind = ProviderKind.OPENAI_COMPATIBLE,
        baseUrl = "https://api.openai.com/v1",
    )

    @Test
    fun officialOpenAiImagesAreMergedIntoExistingCatalog() {
        val existing = listOf(DiscoveredModel("gpt-4.1", "GPT-4.1"))
        val merged = ModelRequestPolicy.mergeOfficialOpenAiCatalog(official.baseUrl, existing)

        assertTrue(merged.any { it.id == "gpt-4.1" })
        assertTrue(merged.any { it.id == "gpt-image-1" && it.supportsImageGeneration == true })
        assertTrue(merged.any { it.id == "gpt-image-1-mini" && it.supportsImageGeneration == true })
    }

    @Test
    fun customCatalogIsNotMutatedByOpenAiHeuristics() {
        val custom = listOf(DiscoveredModel("local-image", "Local Image"))
        val merged = ModelRequestPolicy.mergeOfficialOpenAiCatalog("https://models.example/v1", custom)
        assertEquals(custom, merged)
    }

    @Test
    fun openRouterPresetAndCanonicalUrlUseAutomaticMetadataPolicy() {
        val renamed = ProviderEntity(
            id = "my-router",
            displayName = "My OpenRouter",
            kind = ProviderKind.OPENAI_COMPATIBLE,
            baseUrl = "https://openrouter.ai/api/v1/",
        )

        assertTrue(ModelRequestPolicy.isOpenRouter(renamed))
        assertFalse(ModelRequestPolicy.usesManualRequestType(renamed))
        assertEquals(
            "https://openrouter.ai/api/v1/images",
            ModelRequestPolicy.endpoint(renamed, model("vendor/image-model", image = true, providerId = renamed.id)),
        )
    }

    @Test
    fun officialImageAndChatModelsChooseCorrectEndpoints() {
        val image = model("gpt-image-1", image = false)
        val chat = model("gpt-4.1", image = true)

        assertEquals(ModelRequestType.IMAGE_GENERATION, ModelRequestPolicy.requestType(official, image))
        assertEquals("https://api.openai.com/v1/images/generations", ModelRequestPolicy.endpoint(official, image))
        assertEquals(ModelRequestType.CHAT, ModelRequestPolicy.requestType(official, chat))
        assertEquals("https://api.openai.com/v1/chat/completions", ModelRequestPolicy.endpoint(official, chat))
    }

    @Test
    fun openCodeZenRoutesCurrentModelFamiliesToDocumentedProtocols() {
        val zen = ProviderEntity(
            id = "provider-opencode-zen-test",
            displayName = "OpenCode Zen",
            kind = ProviderKind.OPENAI_COMPATIBLE,
            baseUrl = "https://opencode.ai/zen/v1",
        )

        assertTrue(ModelRequestPolicy.isOpenCodeZen(zen))
        assertFalse(ModelRequestPolicy.usesManualRequestType(zen))
        assertEquals(OpenCodeTransport.RESPONSES, ModelRequestPolicy.openCodeTransport(zen, model("gpt-5.6-sol", false, zen.id)))
        assertEquals(OpenCodeTransport.ANTHROPIC_MESSAGES, ModelRequestPolicy.openCodeTransport(zen, model("claude-sonnet-5", false, zen.id)))
        assertEquals(OpenCodeTransport.ANTHROPIC_MESSAGES, ModelRequestPolicy.openCodeTransport(zen, model("qwen3.7-max", false, zen.id)))
        assertEquals(OpenCodeTransport.GEMINI, ModelRequestPolicy.openCodeTransport(zen, model("gemini-3.8-flash", false, zen.id)))
        assertEquals(OpenCodeTransport.CHAT_COMPLETIONS, ModelRequestPolicy.openCodeTransport(zen, model("deepseek-v4-pro", false, zen.id)))
    }

    @Test
    fun openCodeGoRoutesCurrentModelFamiliesToDocumentedProtocols() {
        val go = ProviderEntity(
            id = "provider-opencode-go-test",
            displayName = "OpenCode Go",
            kind = ProviderKind.OPENAI_COMPATIBLE,
            baseUrl = "https://opencode.ai/zen/go/v1",
        )

        assertTrue(ModelRequestPolicy.isOpenCodeGo(go))
        assertFalse(ModelRequestPolicy.usesManualRequestType(go))
        assertEquals(OpenCodeTransport.RESPONSES, ModelRequestPolicy.openCodeTransport(go, model("gpt-5.6-luna", false, go.id)))
        assertEquals(OpenCodeTransport.CHAT_COMPLETIONS, ModelRequestPolicy.openCodeTransport(go, model("grok-4.5", false, go.id)))
        assertEquals(OpenCodeTransport.RESPONSES, ModelRequestPolicy.openCodeTransport(go, model("grok-4.6", false, go.id)))
        assertEquals(OpenCodeTransport.RESPONSES, ModelRequestPolicy.openCodeTransport(go, model("muse-spark-1.3-contributor", false, go.id)))
        assertEquals(OpenCodeTransport.ANTHROPIC_MESSAGES, ModelRequestPolicy.openCodeTransport(go, model("minimax-m3", false, go.id)))
        assertEquals(OpenCodeTransport.ANTHROPIC_MESSAGES, ModelRequestPolicy.openCodeTransport(go, model("qwen3.8-max", false, go.id)))
        assertEquals(OpenCodeTransport.CHAT_COMPLETIONS, ModelRequestPolicy.openCodeTransport(go, model("kimi-k3", false, go.id)))
        assertEquals(OpenCodeTransport.CHAT_COMPLETIONS, ModelRequestPolicy.openCodeTransport(go, model("glm-5.3", false, go.id)))
    }

    @Test
    fun openCodeDiscoveryEnrichesBothGoAndZenModels() {
        val sparse = DiscoveredModel(
            id = "gpt-5.6-sol",
            displayName = "GPT-5.6 Sol",
        )
        val go = ModelRequestPolicy.enrichOpenCodeModel(
            providerId = "provider-opencode-go-test",
            rawBaseUrl = "https://opencode.ai/zen/go/v1",
            model = sparse,
        )
        val zen = ModelRequestPolicy.enrichOpenCodeModel(
            providerId = "provider-opencode-zen-test",
            rawBaseUrl = "https://opencode.ai/zen/v1",
            model = sparse.copy(id = "claude-sonnet-5", displayName = "Claude Sonnet 5"),
        )

        assertEquals("OpenCode Go", go.metadataSource)
        assertEquals(true, go.supportsThinking)
        assertEquals(true, go.supportsVision)
        assertEquals(true, go.supportsTools)

        assertEquals("OpenCode Zen", zen.metadataSource)
        assertEquals(true, zen.supportsThinking)
        assertEquals(true, zen.supportsVision)
        assertEquals(true, zen.supportsTools)
    }

    @Test
    fun customOpenAiCompatibleProviderUsesCompactPersistedRequestType() {
        val custom = ProviderEntity(
            id = "provider-custom",
            displayName = "Custom",
            kind = ProviderKind.OPENAI_COMPATIBLE,
            baseUrl = "https://models.example/v1",
        )
        assertTrue(ModelRequestPolicy.usesManualRequestType(custom))
        assertEquals(ModelRequestType.IMAGE_GENERATION, ModelRequestPolicy.requestType(custom, model("paint", image = true, providerId = custom.id)))
        assertEquals(ModelRequestType.CHAT, ModelRequestPolicy.requestType(custom, model("chat", image = false, providerId = custom.id)))
        assertFalse(ModelRequestPolicy.usesManualRequestType(official))
    }

    private fun model(id: String, image: Boolean, providerId: String = official.id) = ModelEntity(
        providerId = providerId,
        modelId = id,
        displayName = id,
        contextWindow = 128_000,
        maxOutputTokens = 16_384,
        inputCacheHitUsdPerMillion = 0.0,
        inputCacheMissUsdPerMillion = 0.0,
        outputUsdPerMillion = 0.0,
        supportsImageGeneration = image,
    )
}
