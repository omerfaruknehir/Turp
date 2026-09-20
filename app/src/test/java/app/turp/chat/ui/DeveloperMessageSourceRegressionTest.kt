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
        assertTrue(chat.contains("code = message.content"))
        assertTrue(chat.contains("title = \"MESSAGE SOURCE\""))
        assertTrue(chat.contains("if (sourceVisible) \"Rendered\" else \"Source\""))

        // Raw source must bypass RichMessage while source mode is active.
        val sourceBranch = chat.substringAfter("if (sourceControlsEnabled && sourceVisible)")
            .substringBefore("Row(Modifier.fillMaxWidth().padding(top = 6.dp)")
        assertTrue(sourceBranch.contains("CodeSourcePanel"))
        assertTrue(sourceBranch.contains("message.content"))
        assertFalse(sourceBranch.substringBefore("} else {").contains("RichMessage("))
    }
}
