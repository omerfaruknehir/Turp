package app.turp.chat.provider

import app.turp.chat.data.ProviderEntity
import app.turp.chat.data.ProviderKind
import app.turp.chat.data.ProviderProfile
import app.turp.chat.data.ProviderProtocol
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProviderConfigurationTest {
    @Test
    fun `OpenRouter profile works through arbitrary proxy URL`() {
        val provider = ProviderEntity(
            id = "my-router-proxy",
            displayName = "Router proxy",
            kind = ProviderKind.OPENAI_COMPATIBLE,
            baseUrl = "https://proxy.example.test/router/v1",
            protocol = ProviderProtocol.OPENAI_COMPATIBLE,
            profile = ProviderProfile.OPENROUTER,
        )

        assertTrue(ModelRequestPolicy.isOpenRouter(provider))
        assertEquals(
            "https://proxy.example.test/router/v1/models",
            ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.MODELS),
        )
        assertEquals(
            "https://proxy.example.test/router/v1/key",
            ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.ACCOUNT),
        )
        assertEquals(
            "https://proxy.example.test/router/v1/chat/completions",
            ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.CHAT),
        )
        assertEquals(
            "https://proxy.example.test/router/v1/images/models",
            ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.IMAGE_MODELS),
        )
    }

    @Test
    fun `explicit generic profile wins over hostname inference`() {
        val provider = ProviderEntity(
            id = "generic",
            displayName = "Generic",
            kind = ProviderKind.OPENAI_COMPATIBLE,
            baseUrl = "https://openrouter.ai/api/v1",
            protocol = ProviderProtocol.OPENAI_COMPATIBLE,
            profile = ProviderProfile.GENERIC,
        )

        assertFalse(ModelRequestPolicy.isOpenRouter(provider))
        assertTrue(ModelRequestPolicy.usesManualRequestType(provider))
    }

    @Test
    fun `EVREN profile keeps OpenAI compatible transport and adds EVREN endpoints through proxies`() {
        val provider = ProviderEntity(
            id = "evren-proxy",
            displayName = "EVREN via proxy",
            kind = ProviderKind.OPENAI_COMPATIBLE,
            baseUrl = "https://proxy.example.test/evren/v1",
            protocol = ProviderProtocol.OPENAI_COMPATIBLE,
            profile = ProviderProfile.EVREN,
        )

        assertTrue(ModelRequestPolicy.isEvren(provider))
        assertEquals(ProviderProtocol.OPENAI_COMPATIBLE, provider.effectiveProtocol)
        assertEquals("EVREN", providerProfileLabel(provider.effectiveProfile))
        assertEquals(
            "https://proxy.example.test/evren/v1/chat/completions",
            ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.CHAT),
        )
        assertEquals(
            "https://proxy.example.test/evren/v1/ocr",
            ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.OCR),
        )
        assertEquals(
            "https://proxy.example.test/evren/v1/audio/transcriptions",
            ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.AUDIO_TRANSCRIPTIONS),
        )
        assertEquals(
            "https://proxy.example.test/evren/v1/embeddings",
            ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.EMBEDDINGS),
        )
        assertEquals(
            "https://proxy.example.test/evren/v1/rerank",
            ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.RERANK),
        )
        assertEquals(
            "https://proxy.example.test/evren/v1/terms/status",
            ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.TERMS_STATUS),
        )
        assertEquals(
            "https://proxy.example.test/evren/v1/terms/text",
            ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.TERMS_TEXT),
        )
        assertEquals(
            "https://proxy.example.test/evren/v1/terms/accept",
            ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.TERMS_ACCEPT),
        )
    }

    @Test
    fun `EVREN official base URL is inferred for legacy automatic profile`() {
        val provider = ProviderEntity(
            id = "legacy-evren",
            displayName = "EVREN",
            kind = ProviderKind.OPENAI_COMPATIBLE,
            baseUrl = "https://evren-llmapi.ssyz.org.tr/v1",
            protocol = ProviderProtocol.OPENAI_COMPATIBLE,
            profile = ProviderProfile.AUTO,
        )

        assertTrue(ModelRequestPolicy.isEvrenBaseUrl(provider.baseUrl))
        assertEquals(ProviderProfile.EVREN, provider.effectiveProfile)
        assertTrue(ModelRequestPolicy.isEvren(provider))
    }

    @Test
    fun `relative and absolute endpoint overrides are both supported`() {
        val provider = ProviderEntity(
            id = "proxy",
            displayName = "Proxy",
            kind = ProviderKind.OPENAI_COMPATIBLE,
            baseUrl = "https://proxy.example.test/root",
            protocol = ProviderProtocol.OPENAI_COMPATIBLE,
            profile = ProviderProfile.OPENROUTER,
            endpointOverridesJson = """
                {
                  "models": "catalog/models",
                  "account": "https://accounts.example.test/router/key",
                  "chat": "/gateway/chat"
                }
            """.trimIndent(),
        )

        assertEquals(
            "https://proxy.example.test/root/catalog/models",
            ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.MODELS),
        )
        assertEquals(
            "https://accounts.example.test/router/key",
            ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.ACCOUNT),
        )
        assertEquals(
            "https://proxy.example.test/root/gateway/chat",
            ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.CHAT),
        )
    }

    @Test
    fun `secondary transport endpoints can be independently overridden`() {
        val provider = ProviderEntity(
            id = "router-anywhere",
            displayName = "Router proxy",
            kind = ProviderKind.OPENAI_COMPATIBLE,
            baseUrl = "https://proxy.example.test/root",
            protocol = ProviderProtocol.OPENAI_COMPATIBLE,
            profile = ProviderProfile.OPENROUTER,
            endpointOverridesJson = """
                {
                  "responses": "rpc/responses",
                  "messages": "https://messages.example.test/v2",
                  "geminiStream": "gemini/{model}:stream?alt=sse"
                }
            """.trimIndent(),
        )

        assertEquals(
            "https://proxy.example.test/root/rpc/responses",
            ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.RESPONSES),
        )
        assertEquals(
            "https://messages.example.test/v2",
            ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.MESSAGES),
        )
        assertEquals(
            "https://proxy.example.test/root/gemini/gemini-test:stream?alt=sse",
            ProviderEndpointResolver.resolve(
                provider,
                ProviderEndpointKey.GEMINI_STREAM,
                mapOf("model" to "gemini-test"),
            ),
        )
    }

    @Test
    fun `explicit profile controls behavior even when hostname suggests another provider`() {
        val genericAtRouterHost = ProviderEntity(
            id = "manual-generic",
            displayName = "Generic proxy",
            kind = ProviderKind.OPENAI_COMPATIBLE,
            baseUrl = "https://openrouter.ai/api/v1",
            protocol = ProviderProtocol.OPENAI_COMPATIBLE,
            profile = ProviderProfile.GENERIC,
        )
        val routerAtCustomHost = genericAtRouterHost.copy(
            id = "manual-router",
            baseUrl = "https://llm.example.test/v1",
            profile = ProviderProfile.OPENROUTER,
        )

        assertFalse(ModelRequestPolicy.isOpenRouter(genericAtRouterHost))
        assertTrue(ModelRequestPolicy.isOpenRouter(routerAtCustomHost))
    }

    @Test
    fun `OpenCode V2 server endpoints resolve against configured base`() {
        val provider = ProviderEntity(
            id = "remote-opencode",
            displayName = "Remote OpenCode",
            kind = ProviderKind.OPENAI_COMPATIBLE,
            baseUrl = "https://code.example.test/instance",
            protocol = ProviderProtocol.OPENCODE_V2,
            profile = ProviderProfile.OPENCODE_V2,
        )

        assertEquals(
            "https://code.example.test/instance/api/model",
            ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.MODELS),
        )
        assertEquals(
            "https://code.example.test/instance/api/provider",
            ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.PROVIDERS),
        )
        assertEquals(
            "https://code.example.test/instance/api/experimental/generate",
            ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.GENERATE),
        )
    }

    @Test
    fun `OpenCode Go usage follows proxied base URL`() {
        val provider = ProviderEntity(
            id = "custom-go",
            displayName = "Go proxy",
            kind = ProviderKind.OPENAI_COMPATIBLE,
            baseUrl = "https://go.example.test/v1",
            protocol = ProviderProtocol.OPENAI_COMPATIBLE,
            profile = ProviderProfile.OPENCODE_GO,
        )

        assertEquals(
            "https://go.example.test/v1/usage",
            ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.ACCOUNT),
        )
    }
}
