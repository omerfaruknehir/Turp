package app.turp.chat.provider

import app.turp.chat.data.DefaultCatalog
import app.turp.chat.data.ProviderKind
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenCodeDirectIntegrationTest {
    @Test
    fun `V2 console and go remain separate first class presets on documented wire gateways`() {
        val go = DefaultCatalog.providers.single { it.id == "opencode-go" }
        val zen = DefaultCatalog.providers.single { it.id == "opencode-zen" }

        assertEquals("OpenCode Go", go.displayName)
        // OpenCode V2 Go likewise keeps /zen/go/v1 as its documented direct model gateway.
        assertEquals("https://opencode.ai/zen/go/v1", go.baseUrl)
        assertEquals(ProviderKind.OPENAI_COMPATIBLE, go.kind)
        assertTrue(ModelRequestPolicy.isOpenCodeGo(go))

        assertEquals("OpenCode Zen", zen.displayName)
        // OpenCode V2 changed the OpenCode server/client API, not the Console model gateway.
        // V2 Console documentation still publishes this /zen/v1 endpoint for direct inference.
        assertEquals("https://opencode.ai/zen/v1", zen.baseUrl)
        assertEquals(ProviderKind.OPENAI_COMPATIBLE, zen.kind)
        assertTrue(ModelRequestPolicy.isOpenCodeZen(zen))
    }

    @Test
    fun `provider settings expose go limits and zen billing separately`() {
        val settings = File("src/main/java/app/turp/chat/ui/SettingsScreen.kt").readText()

        assertTrue(settings.contains("\"opencode-go\" to \"OpenCode Go\""))
        assertTrue(settings.contains("\"opencode-zen\" to \"OpenCode Zen\""))
        assertTrue(settings.contains("ModelRequestPolicy.isOpenCodeGo(provider) -> OpenCodeUsagePanel"))
        assertTrue(settings.contains("ModelRequestPolicy.isOpenCodeZen(provider) -> OpenCodeZenBalanceNote"))
        assertTrue(settings.contains("\"5-hour limit\""))
        assertTrue(settings.contains("\"Weekly limit\""))
        assertTrue(settings.contains("\"Monthly limit\""))
        assertFalse(settings.contains("estimated OpenCode Zen balance"))
    }

    @Test
    fun `model discovery enriches native OpenCode lists`() {
        val discovery = File("src/main/java/app/turp/chat/provider/ModelDiscoveryService.kt").readText()

        assertTrue(discovery.contains("ModelRequestPolicy.isOpenCodeGo"))
        assertTrue(discovery.contains("ModelRequestPolicy.isOpenCodeZen"))
        assertTrue(discovery.contains("ModelRequestPolicy.enrichOpenCodeModel(configuredProvider, model)"))
    }

    @Test
    fun `go usage uses the native usage endpoint`() {
        val manager = File("src/main/java/app/turp/chat/provider/OpenCodeManager.kt").readText()

        assertTrue(manager.contains("ProviderEndpointKey.ACCOUNT"))
        assertFalse(manager.contains("https://opencode.ai/zen/go/v1/usage"))
        assertTrue(manager.contains("Authorization"))
        assertTrue(manager.contains("Bearer "))
        assertTrue(manager.contains("rolling"))
        assertTrue(manager.contains("weekly"))
        assertTrue(manager.contains("monthly"))
    }
}
