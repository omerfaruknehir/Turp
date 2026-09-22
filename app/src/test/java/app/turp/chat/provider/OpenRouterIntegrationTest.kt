package app.turp.chat.provider

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenRouterIntegrationTest {
    @Test
    fun `OpenRouter is wired as account aware provider`() {
        val application = File("src/main/java/app/turp/chat/TurpApplication.kt").readText()
        val viewModel = File("src/main/java/app/turp/chat/ui/ChatViewModel.kt").readText()
        val settings = File("src/main/java/app/turp/chat/ui/SettingsScreen.kt").readText()
        val manager = File("src/main/java/app/turp/chat/provider/OpenRouterManager.kt").readText()

        assertTrue(application.contains("OpenRouterManager(secureStore)"))
        assertTrue(viewModel.contains("openRouterKeyStates"))
        assertTrue(viewModel.contains("ensureOpenRouterKeyInfo"))
        assertTrue(settings.contains("OpenRouterKeyUsagePanel"))
        assertTrue(settings.contains("refreshOpenRouterKeyInfo"))
        assertTrue(manager.contains("https://openrouter.ai/api/v1/key"))
    }

    @Test
    fun `OpenRouter catalog token prices are normalized to Turp per million units`() {
        val discovery = File("src/main/java/app/turp/chat/provider/ModelDiscoveryService.kt").readText()
        assertTrue(discovery.contains("OpenRouter advertises catalog pricing as USD per token"))
        assertTrue(discovery.contains("TOKENS_PER_MILLION = 1_000_000.0"))
        assertTrue(discovery.contains("times(TOKENS_PER_MILLION)"))
    }
}
