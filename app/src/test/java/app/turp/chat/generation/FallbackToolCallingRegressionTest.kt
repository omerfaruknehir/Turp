package app.turp.chat.generation

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FallbackToolCallingRegressionTest {
    @Test
    fun generationWorkerPrefersNativeAndCanRetryAsFallback() {
        val worker = File("src/main/java/app/turp/chat/generation/GenerationWorker.kt").readText()

        assertTrue(worker.contains("val executableToolDefinitions"))
        assertTrue(worker.contains("fallbackToolCallingEnabled"))
        assertTrue(worker.contains("var fallbackToolMode"))
        assertTrue(worker.contains("tools = if (nativeToolsDisabled || fallbackToolMode) emptyList() else nativeToolDefinitions"))
        assertTrue(worker.contains("parseFallbackToolCallExact"))
        assertTrue(worker.contains("appendFallbackProtocolInstruction("))
        assertTrue(worker.contains("The provider/API endpoint rejected Turp's native function definitions"))
        assertTrue(worker.contains("fallbackToolMode = true"))
        assertTrue(worker.contains("fallbackToolResultMessage(result)"))
        assertFalse(worker.contains("FallbackToolCallProtocol"))
    }

    @Test
    fun runtimeNativeToolRejectionOffersEnableAndRetry() {
        val chat = File("src/main/java/app/turp/chat/ui/ChatScreen.kt").readText()

        assertTrue(chat.contains("fun shouldOfferToolFallbackForError"))
        assertTrue(chat.contains("\"Enable fallback & retry\""))
        assertTrue(chat.contains("viewModel.enableToolCallFallbackForModel("))
        assertTrue(chat.contains("viewModel.retryMessage(message)"))
        assertTrue(chat.contains("\"Enable fallback for this model\""))
    }

    @Test
    fun fallbackProtocolRuntimeIsStateless() {
        val protocol = File("src/main/java/app/turp/chat/provider/FallbackToolCallProtocol.kt").readText()

        assertFalse(protocol.contains("object FallbackToolCallProtocol"))
        assertFalse(protocol.contains("private val envelope = Regex"))
        assertTrue(protocol.contains("fun parseFallbackToolCallExact("))
        assertTrue(protocol.contains("ProviderJson.parseToJsonElement"))
    }

    @Test
    fun fallbackUsesExistingTurpExecutorInsteadOfASecondExecutor() {
        val worker = File("src/main/java/app/turp/chat/generation/GenerationWorker.kt").readText()

        assertTrue(worker.contains("TurpNativeTools.request(call)"))
        assertTrue(worker.contains("executeTool(parsed.getOrThrow(), call.id, call.argumentsJson)"))
        assertFalse(worker.contains("executeFallbackTool("))
    }
}
