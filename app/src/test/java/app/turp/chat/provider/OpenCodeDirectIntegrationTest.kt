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
    fun `go and zen are separate first class presets`() {
        val go = DefaultCatalog.providers.single { it.id == "opencode-go" }
        val zen = DefaultCatalog.providers.single { it.id == "opencode-zen" }

        assertEquals("OpenCode Go", go.displayName)
        assertEquals("https://opencode.ai/zen/go/v1", go.baseUrl)
        assertEquals(ProviderKind.OPENAI_COMPATIBLE, go.kind)
        assertTrue(ModelRequestPolicy.isOpenCodeGo(go))

        assertEquals("OpenCode Zen", zen.displayName)
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

        assertTrue(discovery.contains("ModelRequestPolicy.isOpenCodeGoBaseUrl(baseUrl)"))
        assertTrue(discovery.contains("ModelRequestPolicy.isOpenCodeZenBaseUrl(baseUrl)"))
        assertTrue(discovery.contains("ModelRequestPolicy.enrichOpenCodeModel(providerId, baseUrl, model)"))
    }

    @Test
    fun `go usage uses the native usage endpoint`() {
        val manager = File("src/main/java/app/turp/chat/provider/OpenCodeManager.kt").readText()

        assertTrue(manager.contains("https://opencode.ai/zen/go/v1/usage"))
        assertTrue(manager.contains("Authorization"))
        assertTrue(manager.contains("Bearer "))
        assertTrue(manager.contains("rolling"))
        assertTrue(manager.contains("weekly"))
        assertTrue(manager.contains("monthly"))
    }
}
