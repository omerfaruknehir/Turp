package app.turp.chat.ui

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MessageSourceDeveloperOptionTest {
    @Test
    fun `developer source includes provider reasoning without changing plain messages`() {
        assertEquals("plain answer", developerMessageSource("plain answer", ""))
        assertEquals(
            "[PROVIDER REASONING]\ninternal reasoning\n\n[MESSAGE CONTENT]\nanswer",
            developerMessageSource("answer", "internal reasoning"),
        )
    }

    @Test
    fun `developer message source option is persisted and wired into chat rendering`() {
        val prefs = File("src/main/java/app/turp/chat/settings/AppPreferences.kt").readText()
        val settings = File("src/main/java/app/turp/chat/ui/SettingsScreen.kt").readText()
        val chat = File("src/main/java/app/turp/chat/ui/ChatScreen.kt").readText()

        assertTrue(prefs.contains("val showMessageSourceEnabled: Boolean = false"))
        assertTrue(prefs.contains("KEY_SHOW_MESSAGE_SOURCE_ENABLED"))
        assertTrue(prefs.contains("putBoolean(KEY_SHOW_MESSAGE_SOURCE_ENABLED, normalized.showMessageSourceEnabled)"))
        assertTrue(prefs.contains("showMessageSourceEnabled = preferences.getBoolean(KEY_SHOW_MESSAGE_SOURCE_ENABLED, false)"))

        assertTrue(settings.contains("\"Show source\""))
        assertTrue(settings.contains("it.copy(showMessageSourceEnabled = enabled)"))

        assertTrue(chat.contains("developerSettings.enabled && developerSettings.showMessageSourceEnabled"))
        assertTrue(chat.contains("CodeSourcePanel("))
        assertTrue(chat.contains("developerMessageSource("))
        assertTrue(chat.contains("reasoning = message.reasoning"))
        assertTrue(chat.contains("if (sourceVisible) \"Rendered\" else \"Source\""))
    }
}
