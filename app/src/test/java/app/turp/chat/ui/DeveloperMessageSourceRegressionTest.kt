package app.turp.chat.ui

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DeveloperMessageSourceRegressionTest {
    @Test
    fun `show source is persisted and gated by developer settings`() {
        val preferences = File("src/main/java/app/turp/chat/settings/AppPreferences.kt").readText()
        val settings = File("src/main/java/app/turp/chat/ui/SettingsScreen.kt").readText()
        val chat = File("src/main/java/app/turp/chat/ui/ChatScreen.kt").readText()

        assertTrue(preferences.contains("val showMessageSourceEnabled: Boolean = false"))
        assertTrue(preferences.contains("KEY_SHOW_MESSAGE_SOURCE_ENABLED"))
        assertTrue(preferences.contains("showMessageSourceEnabled = preferences.getBoolean"))
        assertTrue(preferences.contains("normalized.showMessageSourceEnabled"))

        assertTrue(settings.contains("label = \"Show source\""))
        assertTrue(settings.contains("settings.showMessageSourceEnabled"))
        assertTrue(settings.contains("enabled = settings.enabled"))

        assertTrue(chat.contains("developerSettings.enabled && developerSettings.showMessageSourceEnabled"))
        assertTrue(chat.contains("developerMessageSource("))
        assertTrue(chat.contains("reasoning = message.reasoning"))
        assertTrue(chat.contains("title = \"MESSAGE SOURCE\""))
        assertTrue(chat.contains("if (sourceVisible) \"Rendered\" else \"Source\""))

        // Raw source must bypass RichMessage while source mode is active.
        val sourceBranch = chat.substringAfter("if (sourceControlsEnabled && sourceVisible)")
            .substringBefore("Row(Modifier.fillMaxWidth().padding(top = 6.dp)")
        assertTrue(sourceBranch.contains("CodeSourcePanel"))
        assertTrue(sourceBranch.contains("developerMessageSource"))
        assertTrue(sourceBranch.contains("message.reasoning"))
        assertFalse(sourceBranch.substringBefore("} else {").contains("RichMessage("))
    }

    @Test
    fun `sudo synthetic native calls are not blocked by model metadata`() {
        val worker = java.io.File("src/main/java/app/turp/chat/generation/GenerationWorker.kt").readText()
        assertTrue(worker.contains("if (sudoModeActive && !directImageModel)"))
        assertFalse(worker.contains("if (model.supportsTools && sudoModeActive && !directImageModel)"))
    }    @Test
    fun `developer source can include redacted direct http request`() {
        val source = developerMessageSource(
            content = "answer",
            reasoning = "reason",
            role = "ASSISTANT",
            providerId = "provider",
            modelId = "model",
            status = "COMPLETE",
            toolTraceJson = """[{"tool":"web-search"}]""",
            timelineJson = """[{"providerCallId":"call-1","argumentsJson":"{}","output":"ok"}]""",
            requestSnapshotJson = """{"snapshot":true}""",
            providerCalls = "round: 0\nfinish_reason: stop",
            httpRequest = "POST https://example.test\n\nBody\n{}",
        )
        assertTrue(source.contains("[PROVIDER REASONING]"))
        assertTrue(source.contains("role: ASSISTANT"))
        assertTrue(source.contains("[TOOL TRACE]"))
        assertTrue(source.contains("[MESSAGE TIMELINE · RAW]"))
        assertTrue(source.contains("providerCallId"))
        assertTrue(source.contains("[REQUEST SNAPSHOT]"))
        assertTrue(source.contains("[PROVIDER CALLS]"))
        assertTrue(source.contains("finish_reason: stop"))
        assertTrue(source.contains("[DIRECT HTTP REQUEST · REDACTED]"))
    }

}
