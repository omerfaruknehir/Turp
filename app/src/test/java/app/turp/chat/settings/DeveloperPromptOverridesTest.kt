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
    fun promptInspectorUsesSentAndFamilyBasedCustomizeViews() {
        val settings = File("src/main/java/app/turp/chat/ui/SettingsScreen.kt").readText()

        assertTrue(settings.contains("\"Sent to model\""))
        assertTrue(settings.contains("\"Customize\""))
        assertTrue(settings.contains("\"Main prompts\""))
        assertTrue(settings.contains("\"Advanced internals (\""))
        assertTrue(settings.contains("\"Prompt inspector\""))
        assertTrue(settings.contains("\"Open system context\""))
        assertTrue(settings.contains("DeveloperPromptInspectorSheet"))
        assertTrue(settings.contains("DeveloperPromptComponentEditorSheet"))
        assertTrue(settings.contains("\"Restore built-in\""))
        assertTrue(settings.contains("\"Include this layer\""))
        assertTrue(settings.contains("\"Variables (\""))
        assertTrue(settings.contains("\"Preview\""))

        assertFalse(settings.contains("DeveloperPromptManagerSheet"))
        assertFalse(settings.contains("DeveloperPromptContextSheet"))
        assertFalse(settings.contains("\"Effective context\""))
        assertFalse(settings.contains("\"Search prompts\""))
        assertFalse(settings.contains("\"Customized only\""))
        assertFalse(settings.contains("promptMenuExpanded"))
        assertFalse(settings.contains("DEVELOPER_PROMPT_DEFAULT_TOKEN"))
    }

    @Test
    fun everyRegisteredPromptKeyIsAssignedToExactlyOneInspectorFamily() {
        val settings = File("src/main/java/app/turp/chat/ui/SettingsScreen.kt").readText()

        DeveloperPromptKey.entries.forEach { key ->
            val needle = "DeveloperPromptKey.${key.name}"
            val familySection = settings.substring(
                settings.indexOf("private val developerPromptMainFamilies"),
                settings.indexOf("private fun promptCustomizationStatus"),
            )
            assertEquals(
                "Expected exactly one family assignment for ${key.name}",
                1,
                Regex(Regex.escape(needle) + "(?![A-Z0-9_])").findAll(familySection).count(),
            )
        }
    }

    @Test
    fun primaryCustomizeViewKeepsInternalPayloadsOutOfMainFamilies() {
        val settings = File("src/main/java/app/turp/chat/ui/SettingsScreen.kt").readText()
        val mainStart = settings.indexOf("private val developerPromptMainFamilies")
        val advancedStart = settings.indexOf("private val developerPromptAdvancedFamilies")
        val main = settings.substring(mainStart, advancedStart)
        val advanced = settings.substring(advancedStart, settings.indexOf("private val developerPromptAllFamilies"))

        assertFalse(main.contains("DeveloperPromptKey.MEMORY_CONTEXT"))
        assertFalse(main.contains("DeveloperPromptKey.GENERATED_CONTENT"))
        assertTrue(advanced.contains("DeveloperPromptKey.MEMORY_CONTEXT"))
        assertTrue(advanced.contains("DeveloperPromptKey.GENERATED_CONTENT"))
        assertTrue(main.contains("DeveloperPromptKey.CORE_PROMPT"))
        assertTrue(main.contains("DeveloperPromptKey.RUNTIME_CONTEXT"))
        assertTrue(main.contains("DeveloperPromptKey.TOOL_NATIVE"))
        assertTrue(main.contains("DeveloperPromptKey.DEEP_RESEARCH"))
        assertTrue(main.contains("DeveloperPromptKey.SUDO_LAYER"))
    }

    @Test
    fun promptPersistenceKeepsDisabledStateSeparateAndMigratesLegacyEmptyValues() {
        val prefs = File("src/main/java/app/turp/chat/settings/AppPreferences.kt").readText()
        val viewModel = File("src/main/java/app/turp/chat/ui/ChatViewModel.kt").readText()

        assertTrue(prefs.contains("KEY_DEVELOPER_PROMPT_DISABLED_IDS"))
        assertTrue(prefs.contains("fun setDeveloperPromptDisabled"))
        assertTrue(prefs.contains("shouldEnable = value != null"))
        assertTrue(prefs.contains("if (disabled) putBoolean(KEY_DEVELOPER_PROMPT_OVERRIDES_ENABLED, true)"))
        assertTrue(prefs.contains("fun resetDeveloperPromptCustomization"))
        assertTrue(prefs.contains("legacyDisabled"))
        assertTrue(viewModel.contains("fun setDeveloperPromptDisabled"))
        assertTrue(viewModel.contains("fun resetDeveloperPromptCustomization"))
    }

    @Test
    fun everyPromptHasNonEmptySourceTemplate() {
        DeveloperPromptKey.entries.forEach { key ->
            val spec = DeveloperPromptTemplateCatalog.spec(key)
            assertTrue("Template missing for ${key.id}", spec.template.isNotBlank())
        }
    }

    @Test
    fun primaryDynamicPromptsExposeMeaningfulSourceTemplates() {
        val research = DeveloperPromptTemplateCatalog.spec(DeveloperPromptKey.DEEP_RESEARCH).template
        val sudo = DeveloperPromptTemplateCatalog.spec(DeveloperPromptKey.SUDO_LAYER).template
        val style = DeveloperPromptTemplateCatalog.spec(DeveloperPromptKey.RESPONSE_STYLE).template

        assertTrue(research.contains("Deep Research mode is active"))
        assertFalse(research.trim() == "{{deep_research_instructions}}")
        assertTrue(sudo.contains("Turp Sudo mode is active"))
        assertTrue(sudo.contains("{{sudo_user_instruction}}"))
        assertTrue(style.contains("Less emoji is enabled"))
    }

    @Test
    fun namedVariablesRenderAndUnknownVariablesArePreserved() {
        val template = "version={{app_version}} missing={{not_available}}"
        assertEquals(
            "version=0.25.1 missing={{not_available}}",
            DeveloperPromptVariables.render(template, mapOf("app_version" to "0.25.1")),
        )
        assertEquals(
            setOf("app_version", "not_available"),
            DeveloperPromptVariables.names(template),
        )
        assertEquals(
            setOf("not_available"),
            DeveloperPromptVariables.unresolved(template, mapOf("app_version" to "0.25.1")),
        )
    }

    @Test
    fun savedPromptTemplatesResolveRuntimeVariables() {
        val edits = DeveloperPromptOverrides(
            enabled = true,
            values = mapOf(
                DeveloperPromptKey.RUNTIME_CONTEXT.id to
                    "Turp {{app_version}} · {{timezone}} · {{unknown_variable}}",
            ),
        )
        assertEquals(
            "Turp 0.25.1 · Europe/Istanbul · {{unknown_variable}}",
            edits.resolve(
                DeveloperPromptKey.RUNTIME_CONTEXT,
                "built-in",
                mapOf(
                    "app_version" to "0.25.1",
                    "timezone" to "Europe/Istanbul",
                ),
            ),
        )
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
        assertTrue(traceStore.contains("val sourceTemplate: String"))
        assertTrue(traceStore.contains("val variables: Map<String, String>"))
        assertTrue(traceStore.contains("fun recordComponent("))
        assertTrue(httpStore.contains("DeveloperPromptTraceStore.recordProviderContext(request, protocol)"))
        assertTrue(worker.contains("developerPromptTraceEnabled = developerSettings.enabled"))
        assertTrue(traceStore.contains("val modelId: String"))
        assertTrue(settings.contains("\"Sent to model\""))
        assertTrue(settings.contains("provider-boundary context"))
        assertTrue(settings.contains("Provider-owned upstream instructions"))
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
