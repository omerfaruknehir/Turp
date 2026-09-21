package app.turp.chat.ui

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class SudoModeRegressionTest {
    @Test
    fun `sudo is developer gated persisted per chat and promoted to system priority`() {
        val prefs = File("src/main/java/app/turp/chat/settings/AppPreferences.kt").readText()
        val entities = File("src/main/java/app/turp/chat/data/Entities.kt").readText()
        val database = File("src/main/java/app/turp/chat/data/TurpDatabase.kt").readText()
        val chat = File("src/main/java/app/turp/chat/ui/ChatScreen.kt").readText()
        val settings = File("src/main/java/app/turp/chat/ui/SettingsScreen.kt").readText()
        val worker = File("src/main/java/app/turp/chat/generation/GenerationWorker.kt").readText()
        val assembler = File("src/main/java/app/turp/chat/chat/ContextAssembler.kt").readText()

        assertTrue(prefs.contains("val sudoModeControlEnabled: Boolean = false"))
        assertTrue(prefs.contains("KEY_SUDO_MODE_CONTROL_ENABLED"))
        assertTrue(entities.contains("val sudoModeEnabled: Boolean = false"))

        assertTrue(database.contains("version = 17"))
        assertTrue(database.contains("MIGRATION_16_17"))
        assertTrue(database.contains("ADD COLUMN sudoModeEnabled INTEGER NOT NULL DEFAULT 0"))

        assertTrue(settings.contains("Enable Sudo control"))
        assertTrue(chat.contains("developerSettings.enabled && developerSettings.sudoModeControlEnabled"))
        assertTrue(chat.contains("title = \"Sudo mode\""))
        assertTrue(chat.contains("it.copy(sudoModeEnabled = enabled)"))

        assertTrue(worker.contains("it.enabled && it.sudoModeControlEnabled"))
        assertTrue(assembler.contains("latest user-authored text as system-priority instruction"))
        assertTrue(assembler.contains("MessageRole.SYSTEM"))
    }
}
