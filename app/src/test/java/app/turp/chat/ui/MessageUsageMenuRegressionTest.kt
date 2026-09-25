package app.turp.chat.ui

import app.turp.chat.data.MessageEntity
import app.turp.chat.data.MessageRole
import app.turp.chat.data.MessageStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class MessageUsageMenuRegressionTest {
    private fun message(
        nodeId: String,
        parentNodeId: String?,
        role: MessageRole,
        createdAt: Long,
        superseded: Boolean = false,
    ) = MessageEntity(
        rowId = createdAt,
        nodeId = nodeId,
        conversationId = "conversation",
        parentNodeId = parentNodeId,
        branchId = "branch-$nodeId",
        role = role,
        content = nodeId,
        status = MessageStatus.COMPLETE,
        createdAt = createdAt,
        updatedAt = createdAt,
        supersededAt = if (superseded) createdAt + 100 else null,
    )

    private fun repositoryFile(path: String): File = sequenceOf(File(path), File("..", path))
        .firstOrNull(File::isFile)
        ?: error("Could not locate repository file: $path")

    @Test
    fun `message footer exposes usage and share overflow menu`() {
        val chat = repositoryFile("app/src/main/java/app/turp/chat/ui/ChatScreen.kt").readText()
        val usageUi = repositoryFile("app/src/main/java/app/turp/chat/ui/UsageDetailsUi.kt").readText()

        assertTrue(chat.contains("MessageContextMenu(message)"))
        assertTrue(usageUi.contains("Box {\n        IconButton(onClick = { open = true }"))
        assertTrue(usageUi.contains("Text(\"Usage details\")"))
        assertTrue(usageUi.contains("Text(\"Share message\")"))
        assertTrue(usageUi.contains("Text(\"Request & response\")"))
        assertTrue(usageUi.contains("developerSettings.showHttpRequestEnabled"))
        assertTrue(usageUi.contains("DeveloperHttpTraceStore.traces.collectAsState()"))
        assertTrue(usageUi.contains("attachmentDao().forMessage(message.nodeId)"))
        assertTrue(usageUi.contains("Intent.ACTION_SEND_MULTIPLE"))
        assertTrue(usageUi.contains("Non-cached input"))
        assertTrue(usageUi.contains("Provider calls"))
        assertTrue(usageUi.contains(".heightIn(max = 260.dp)"))
        assertTrue(usageUi.contains(".verticalScroll(rememberScrollState())"))
    }
    @Test
    fun `http trace owner maps user messages to active assistant response`() {
        val user = message("user", null, MessageRole.USER, 1)
        val oldRetry = message("assistant-old", "user", MessageRole.ASSISTANT, 2, superseded = true)
        val active = message("assistant-active", "user", MessageRole.ASSISTANT, 3)
        val unrelated = message("assistant-other", "other-user", MessageRole.ASSISTANT, 4)

        assertEquals(
            "assistant-active",
            httpTraceOwnerNodeId(user, listOf(oldRetry, unrelated, active)),
        )
        assertEquals(
            "assistant-active",
            httpTraceOwnerNodeId(active, listOf(user, oldRetry, unrelated)),
        )
    }

    @Test
    fun `http trace owner falls back to newest response when branch metadata is legacy`() {
        val user = message("user", null, MessageRole.USER, 1)
        val first = message("assistant-1", "user", MessageRole.ASSISTANT, 2, superseded = true)
        val second = message("assistant-2", "user", MessageRole.ASSISTANT, 5, superseded = true)

        assertEquals(
            "assistant-2",
            httpTraceOwnerNodeId(user, listOf(first, second)),
        )
    }

}
