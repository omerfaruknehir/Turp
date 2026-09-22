package app.turp.chat.settings

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DeveloperPromptOverridesTest {
    @Test
    fun overridesSupportDefaultWrappingReplacementAndRemoval() {
        val disabled = DeveloperPromptOverrides(
            enabled = false,
            values = mapOf(DeveloperPromptKey.CORE_PROMPT.id to "ignored"),
        )
        assertEquals("default", disabled.resolve(DeveloperPromptKey.CORE_PROMPT, "default"))

        val wrapped = DeveloperPromptOverrides(
            enabled = true,
            values = mapOf(DeveloperPromptKey.CORE_PROMPT.id to "before {{default}} after"),
        )
        assertEquals("before default after", wrapped.resolve(DeveloperPromptKey.CORE_PROMPT, "default"))

        val replaced = DeveloperPromptOverrides(
            enabled = true,
            values = mapOf(DeveloperPromptKey.CORE_PROMPT.id to "replacement"),
        )
        assertEquals("replacement", replaced.resolve(DeveloperPromptKey.CORE_PROMPT, "default"))

        val removed = DeveloperPromptOverrides(
            enabled = true,
            values = mapOf(DeveloperPromptKey.CORE_PROMPT.id to ""),
        )
        assertEquals("", removed.resolve(DeveloperPromptKey.CORE_PROMPT, "default"))
    }

    @Test
    fun everyRegisteredLayerIsReachableFromDeveloperEditor() {
        val settings = File("src/main/java/app/turp/chat/ui/SettingsScreen.kt").readText()
        assertTrue(settings.contains("DeveloperPromptKey.entries.forEach"))
        assertTrue(settings.contains("setDeveloperPromptOverride"))
        assertTrue(settings.contains("resetDeveloperPromptOverrides"))
        assertTrue(settings.contains("DEVELOPER_PROMPT_DEFAULT_TOKEN"))

        val required = setOf(
            DeveloperPromptKey.CORE_PROMPT,
            DeveloperPromptKey.RUNTIME_CONTEXT,
            DeveloperPromptKey.TOOL_NATIVE_SUDO,
            DeveloperPromptKey.DEEP_RESEARCH,
            DeveloperPromptKey.MEMORY_CONTEXT,
            DeveloperPromptKey.GENERATED_CONTENT,
            DeveloperPromptKey.SUDO_LAYER,
            DeveloperPromptKey.WORKING_CONTEXT,
            DeveloperPromptKey.RESEARCH_INITIAL,
            DeveloperPromptKey.RESEARCH_FINAL,
            DeveloperPromptKey.DEEPSEEK_TOOL_GUARD,
            DeveloperPromptKey.AUX_TITLE,
            DeveloperPromptKey.AUX_COMPRESSION,
            DeveloperPromptKey.FINAL_SYSTEM_MESSAGE,
        )
        assertTrue(DeveloperPromptKey.entries.containsAll(required))
        assertFalse(DeveloperPromptKey.entries.any { it.id.isBlank() || it.title.isBlank() })
    }

    @Test
    fun providerStageToolPromptsAreDeveloperEditable() {
        val provider = File("src/main/java/app/turp/chat/provider/OpenAiCompatibleProvider.kt").readText()
        val worker = File("src/main/java/app/turp/chat/generation/GenerationWorker.kt").readText()
        assertTrue(worker.contains("DeveloperPromptKey.DEEPSEEK_TOOL_GUARD"))
        assertTrue(worker.contains("DeveloperPromptKey.DEEPSEEK_TOOL_CORRECTION"))
        assertTrue(worker.contains("DeveloperPromptKey.TOOL_DISABLED_PROTOCOL_CORRECTION"))
        assertTrue(provider.contains("request.deepSeekToolGuardPrompt"))
        assertTrue(provider.contains("request.deepSeekToolCorrectionPrompt"))
        assertTrue(provider.contains("request.toolDisabledProtocolCorrectionPrompt"))
    }
}
