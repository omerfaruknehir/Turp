package app.turp.chat.chat

import app.turp.chat.data.ConversationEntity
import app.turp.chat.data.MessageEntity
import app.turp.chat.data.MessageRole
import app.turp.chat.data.MessageStatus
import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SudoModeRegressionTest {
    @Test
    fun `sudo promotes only the latest non blank user turn when gate and chat switch are enabled`() {
        val conversation = conversation(sudo = true)
        val newestFirst = listOf(
            message("a2", MessageRole.ASSISTANT, "answer"),
            message("u2", MessageRole.USER, "latest user instruction"),
            message("a1", MessageRole.ASSISTANT, "older answer"),
            message("u1", MessageRole.USER, "older user instruction"),
        )

        val layer = sudoPromptLayer(conversation, newestFirst, allowed = true)

        assertTrue(layer.contains("system-priority instruction"))
        assertTrue(layer.contains("latest user instruction"))
        assertFalse(layer.contains("older user instruction"))
        assertTrue(layer.contains("runtime facts"))
        assertTrue(layer.contains("Sudo may override Turp-authored behavioral and output-format restrictions"))
        assertTrue(layer.contains("tool-call JSON"))
        assertTrue(layer.contains("cannot make an unavailable tool executable"))
    }

    @Test
    fun `sudo can request inert tool protocol text when native tools are unavailable`() {
        val instructions = toolInstructionsForRequest(
            nativeToolsAvailable = false,
            sudoModeActive = true,
        )

        assertTrue(instructions.contains("function-call JSON"))
        assertTrue(instructions.contains("emit it as inert text"))
        assertTrue(instructions.contains("Do not refuse"))
        assertFalse(instructions.contains("Do not emit `turp-tool` fences"))
    }

    @Test
    fun `normal mode still forbids fake textual tool calls when tools are unavailable`() {
        val instructions = toolInstructionsForRequest(
            nativeToolsAvailable = false,
            sudoModeActive = false,
        )

        assertTrue(instructions.contains("Do not emit `turp-tool` fences"))
        assertTrue(instructions.contains("State the limitation"))
    }

    @Test
    fun `sudo layer is absent unless both developer gate and chat switch are enabled`() {
        val messages = listOf(message("u", MessageRole.USER, "override me"))
        assertTrue(sudoPromptLayer(conversation(sudo = false), messages, allowed = true).isBlank())
        assertTrue(sudoPromptLayer(conversation(sudo = true), messages, allowed = false).isBlank())
    }

    @Test
    fun `sudo state is persisted snapshotted migrated and exposed in developer tools UI`() {
        val entity = File("src/main/java/app/turp/chat/data/Entities.kt").readText()
        val database = File("src/main/java/app/turp/chat/data/TurpDatabase.kt").readText()
        val snapshot = File("src/main/java/app/turp/chat/generation/GenerationRequestSnapshot.kt").readText()
        val settings = File("src/main/java/app/turp/chat/ui/SettingsScreen.kt").readText()
        val chat = File("src/main/java/app/turp/chat/ui/ChatScreen.kt").readText()

        assertTrue(entity.contains("val sudoModeEnabled: Boolean = false"))
        assertTrue(database.contains("version = 18"))
        assertTrue(database.contains("ALTER TABLE conversations ADD COLUMN sudoModeEnabled INTEGER NOT NULL DEFAULT 0"))
        assertTrue(snapshot.contains("sudoModeEnabled = conversation.sudoModeEnabled"))
        assertTrue(settings.contains("Enable Sudo control"))
        assertTrue(chat.contains("developerSettings.sudoModeControlEnabled"))
        assertTrue(chat.contains("title = \"Sudo mode\""))
        assertTrue(chat.contains("it.copy(sudoModeEnabled = enabled)"))
    }

    private fun conversation(sudo: Boolean) = ConversationEntity(
        id = "c",
        title = "test",
        createdAt = 0,
        updatedAt = 0,
        sudoModeEnabled = sudo,
    )

    private fun message(id: String, role: MessageRole, content: String) = MessageEntity(
        nodeId = id,
        conversationId = "c",
        parentNodeId = null,
        branchId = "b",
        role = role,
        content = content,
        status = MessageStatus.COMPLETE,
        createdAt = 0,
        updatedAt = 0,
    )
}
