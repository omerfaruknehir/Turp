package app.turp.chat.provider

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProviderInstancePresetTest {
    @Test
    fun `unique connection ids retain preset semantics`() {
        listOf("openai", "deepseek", "openrouter", "groq", "mistral", "xai", "qwen-cloud", "ollama").forEach { preset ->
            assertTrue(ModelRequestPolicy.matchesPresetId(preset, preset))
            assertTrue(ModelRequestPolicy.matchesPresetId("provider-$preset-1234", preset))
        }
        assertFalse(ModelRequestPolicy.matchesPresetId("provider-gemini-1234", "deepseek"))
        assertFalse(ModelRequestPolicy.matchesPresetId("custom-openai-compatible", "openai"))
    }

    @Test
    fun `provider preset selection is reusable and reaches the sheet`() {
        val source = File("src/main/java/app/turp/chat/ui/SettingsScreen.kt").readText()
        assertTrue(source.contains("initialTemplateId = addingProviderTemplateId"))
        assertTrue(source.contains("templates = DefaultCatalog.providers.filter"))
        assertTrue(source.contains("provider-${'$'}{templateId ?: draft.protocol.name.lowercase()}-${'$'}{UUID.randomUUID()}"))
    }
}
