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
        assertTrue(worker.contains("FallbackToolCallProtocol.parseExact"))
        assertTrue(worker.contains("appendFallbackProtocolInstruction("))
        assertTrue(worker.contains("The provider/API endpoint rejected Turp's native function definitions"))
        assertTrue(worker.contains("fallbackToolMode = true"))
        assertTrue(worker.contains("FallbackToolCallProtocol.resultMessage(result)"))
    }

    @Test
    fun fallbackUsesExistingTurpExecutorInsteadOfASecondExecutor() {
        val worker = File("src/main/java/app/turp/chat/generation/GenerationWorker.kt").readText()

        assertTrue(worker.contains("TurpNativeTools.request(call)"))
        assertTrue(worker.contains("executeTool(parsed.getOrThrow(), call.id, call.argumentsJson)"))
        assertFalse(worker.contains("executeFallbackTool("))
    }
}
