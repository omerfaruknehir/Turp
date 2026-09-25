package app.turp.chat.files

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AttachmentSizePolicyRegressionTest {
    @Test
    fun `local attachments have no arbitrary byte quota`() {
        val store = File("src/main/java/app/turp/chat/files/AttachmentStore.kt").readText()
        val tools = File("src/main/java/app/turp/chat/agent/AgentTools.kt").readText()

        listOf(
            "MAX_FILE_BYTES",
            "MAX_CHAT_ATTACHMENT_BYTES",
            "MAX_APP_ATTACHMENT_BYTES",
            "Files are limited to 64 MB",
            "Generated images are limited to 64 MB",
            "This chat has reached its 512 MB attachment limit",
            "Turp's 2 GB attachment storage limit has been reached",
        ).forEach { obsolete ->
            assertFalse("obsolete local byte quota remains: $obsolete", store.contains(obsolete))
        }
        assertFalse(tools.contains("AttachmentStore.MAX_FILE_BYTES"))
        assertFalse(tools.contains("Returned files are limited to 64 MB"))

        assertTrue(store.contains("Not enough free storage to attach this file and make its workspace copy"))
        assertTrue(store.contains("Not enough free storage to make this file's workspace copy"))
        assertTrue(store.contains("Not enough free storage to save the generated image"))
        assertTrue(store.contains("MAX_STAGED_ATTACHMENTS = 12"))
    }
}
