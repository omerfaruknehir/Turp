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
    fun disabledStateIsIndependentFromSavedEdit() {
        val customized = DeveloperPromptOverrides(
            enabled = true,
            values = mapOf(DeveloperPromptKey.CORE_PROMPT.id to "my edited prompt"),
            disabledIds = setOf(DeveloperPromptKey.CORE_PROMPT.id),
        )

        assertTrue(customized.hasOverride(DeveloperPromptKey.CORE_PROMPT))
        assertTrue(customized.isDisabled(DeveloperPromptKey.CORE_PROMPT))
        assertTrue(customized.isCustomized(DeveloperPromptKey.CORE_PROMPT))
        assertEquals("", customized.resolve(DeveloperPromptKey.CORE_PROMPT, "built-in"))
        assertEquals(
            "my edited prompt",
            customized.editorText(DeveloperPromptKey.CORE_PROMPT, "built-in"),
        )

        val enabledAgain = customized.copy(disabledIds = emptySet())
        assertEquals(
            "my edited prompt",
            enabledAgain.resolve(DeveloperPromptKey.CORE_PROMPT, "built-in"),
        )
    }

    @Test
    fun everyRegisteredLayerIsReachableFromDeveloperEditor() {
        val settings = File("src/main/java/app/turp/chat/ui/SettingsScreen.kt").readText()
        assertTrue(settings.contains("DeveloperPromptKey.entries.forEach"))
        assertTrue(settings.contains("setDeveloperPromptOverride"))
        assertTrue(settings.contains("resetDeveloperPromptOverrides"))
        assertTrue(settings.contains("\"Manage prompts\""))
        assertTrue(settings.contains("\"Search prompts\""))
        assertTrue(settings.contains("\"Customized only\""))
        assertTrue(settings.contains("\"Use built-in\""))
        assertTrue(settings.contains("\"Component enabled\""))
        assertTrue(settings.contains("\"Reset component\""))
        assertTrue(settings.contains("\"Reset all prompt customizations?\""))
        assertTrue(settings.contains("\"Disabled · edit saved\""))
        assertTrue(settings.contains("\"Effective system context\""))
        assertTrue(settings.contains("DeveloperPromptGroup.entries.forEach"))
        assertFalse(settings.contains("promptMenuExpanded"))
        assertFalse(settings.contains("DEVELOPER_PROMPT_DEFAULT_TOKEN"))

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
    fun promptPersistenceKeepsDisabledStateSeparateAndMigratesLegacyEmptyValues() {
        val prefs = File("src/main/java/app/turp/chat/settings/AppPreferences.kt").readText()
        val viewModel = File("src/main/java/app/turp/chat/ui/ChatViewModel.kt").readText()

        assertTrue(prefs.contains("KEY_DEVELOPER_PROMPT_DISABLED_IDS"))
        assertTrue(prefs.contains("fun setDeveloperPromptDisabled"))
        assertTrue(prefs.contains("fun resetDeveloperPromptCustomization"))
        assertTrue(prefs.contains("legacyDisabled"))
        assertTrue(viewModel.contains("fun setDeveloperPromptDisabled"))
        assertTrue(viewModel.contains("fun resetDeveloperPromptCustomization"))
    }

    @Test
    fun directEditorExpandsLegacyTemplatesIntoConcreteText() {
        val edits = DeveloperPromptOverrides(
            enabled = true,
            values = mapOf(DeveloperPromptKey.CORE_PROMPT.id to "before {{default}} after"),
        )
        assertEquals(
            "before built-in prompt after",
            edits.editorText(DeveloperPromptKey.CORE_PROMPT, "built-in prompt"),
        )
        assertEquals(
            "built-in prompt",
            DeveloperPromptOverrides(enabled = true)
                .editorText(DeveloperPromptKey.CORE_PROMPT, "built-in prompt"),
        )
    }

    @Test
    fun effectiveContextHasProviderBoundaryTraceSupport() {
        val traceStore = File("src/main/java/app/turp/chat/settings/DeveloperPromptOverrides.kt").readText()
        val httpStore = File("src/main/java/app/turp/chat/provider/DeveloperHttpTraceStore.kt").readText()
        val settings = File("src/main/java/app/turp/chat/ui/SettingsScreen.kt").readText()
        val worker = File("src/main/java/app/turp/chat/generation/GenerationWorker.kt").readText()

        assertTrue(traceStore.contains("fun recordProviderContext(request: ChatRequest, protocol: String)"))
        assertTrue(traceStore.contains("stage = \"PROVIDER_BOUNDARY\""))
        assertTrue(traceStore.contains("data class DeveloperPromptComponentTrace"))
        assertTrue(traceStore.contains("fun recordComponent("))
        assertTrue(httpStore.contains("DeveloperPromptTraceStore.recordProviderContext(request, protocol)"))
        assertTrue(worker.contains("developerPromptTraceEnabled = developerSettings.enabled"))
        assertTrue(settings.contains("\"Effective system context\""))
        assertTrue(settings.contains("Provider/API-owned upstream system or developer instructions"))
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
