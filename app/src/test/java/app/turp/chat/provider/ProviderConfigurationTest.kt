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
