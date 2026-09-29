package app.turp.chat.ui

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class ErrorDetailsUiRegressionTest {
    @Test
    fun inlineRecoveryCardAlwaysExposesNormalUserDetails() {
        val chat = File("src/main/java/app/turp/chat/ui/ChatScreen.kt").readText()

        assertTrue(chat.contains("var errorDetailsOpen"))
        assertTrue(chat.contains("onClick = { errorDetailsOpen = true }"))
        assertTrue(chat.contains("MessageErrorDetailsDialog("))
        assertTrue(chat.contains("\"Details\""))
    }

    @Test
    fun errorDetailsShowDurableProviderAttemptDiagnostics() {
        val usage = File("src/main/java/app/turp/chat/ui/UsageDetailsUi.kt").readText()

        assertTrue(usage.contains("fun MessageErrorDetailsDialog("))
        assertTrue(usage.contains("repository.generationUsage(message.nodeId)"))
        assertTrue(usage.contains("\"Full error\""))
        assertTrue(usage.contains("\"Provider attempts\""))
        assertTrue(usage.contains("call.finishReason"))
        assertTrue(usage.contains("call.error"))
        assertTrue(usage.contains("\"Turp request error details\""))
    }

    @Test
    fun emptyStreamFailurePreservesLastProviderEvent() {
        val provider = File("src/main/java/app/turp/chat/provider/OpenAiCompatibleProvider.kt").readText()

        assertTrue(provider.contains("var lastStreamEvent: String?"))
        assertTrue(provider.contains("lastStreamEvent = payload.take(MAX_STREAM_DIAGNOSTIC_CHARS)"))
        assertTrue(provider.contains("val emptyStreamDiagnostics = mutableListOf<String>()"))
        assertTrue(provider.contains("append(\"stream attempt \")"))
        assertTrue(provider.contains("append(\"\\n  last event: \")"))
        assertTrue(provider.contains("nonStreamingCompatibilityRetry("))
        assertTrue(provider.contains("MAX_STREAM_DIAGNOSTIC_CHARS = 1_500"))
    }
}
