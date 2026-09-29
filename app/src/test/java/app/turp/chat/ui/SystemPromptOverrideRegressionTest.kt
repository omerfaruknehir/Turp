package app.turp.chat.ui

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SystemPromptOverrideRegressionTest {
    @Test
    fun `override replaces the built in prompt while prepend keeps it`() {
        val assembler = File("src/main/java/app/turp/chat/chat/ContextAssembler.kt").readText()

        assertTrue(assembler.contains("val overrideProfile = promptProfile?.mode == SystemPromptMode.OVERRIDE"))
        assertTrue(assembler.contains("val basePrompt = if (overrideProfile)"))
        assertTrue(assembler.contains("customProfileInstructions"))
        assertTrue(assembler.contains("DEFAULT_TURP_SYSTEM_PROMPT"))
        assertTrue(assembler.contains("val resolvedBasePrompt = promptLayer("))
        assertTrue(assembler.contains("DeveloperPromptKey.CUSTOM_PROFILE_OVERRIDE"))
        assertTrue(assembler.contains("DeveloperPromptKey.CORE_PROMPT"))
        assertTrue(assembler.contains("\$resolvedBasePrompt"))
        assertFalse(assembler.contains("This profile may override Turp's default tone/persona preferences only"))
    }

    @Test
    fun `editor can fork the current Turp system prompt as an editable override`() {
        val settings = File("src/main/java/app/turp/chat/ui/SettingsScreen.kt").readText()

        assertTrue(settings.contains("Use Turp template"))
        assertTrue(settings.contains("prompt = DEFAULT_TURP_SYSTEM_PROMPT"))
        assertTrue(settings.contains("mode = SystemPromptMode.OVERRIDE"))
        assertTrue(settings.contains("Override replaces the built-in prompt text"))
        assertTrue(settings.contains("Developer Options → System prompts lets you inspect and directly edit"))
        assertFalse(settings.contains("Those dynamic layers are not editable either"))
    }
}
