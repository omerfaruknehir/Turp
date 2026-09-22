package app.turp.chat.provider

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DeveloperHttpTraceStoreTest {
    @Test
    fun redactsSensitiveJsonFieldsRecursively() {
        val raw = """{"model":"test","api_key":"secret","nested":{"access_token":"token","safe":"visible"}}"""
        val redacted = DeveloperHttpTraceStore.redactBody(raw)

        assertTrue(redacted.contains("[REDACTED]"))
        assertTrue(redacted.contains("visible"))
        assertTrue(redacted.contains("test"))
        assertFalse(redacted.contains("secret"))
        assertFalse(redacted.contains("\"token\""))
    }

    @Test
    fun sudoProvidedToolsAreSerializedEvenWhenCatalogMetadataDisagrees() {
        val openAi = java.io.File("src/main/java/app/turp/chat/provider/OpenAiCompatibleProvider.kt").readText()
        val anthropic = java.io.File("src/main/java/app/turp/chat/provider/AnthropicProvider.kt").readText()
        val gemini = java.io.File("src/main/java/app/turp/chat/provider/GeminiProvider.kt").readText()
        val oauth = java.io.File("src/main/java/app/turp/chat/provider/OpenAiOAuthProvider.kt").readText()

        assertTrue(openAi.contains("if (request.tools.isNotEmpty())"))
        assertFalse(openAi.contains("request.tools.isNotEmpty() && request.model.supportsTools"))
        assertTrue(anthropic.contains("if (request.tools.isNotEmpty())"))
        assertFalse(anthropic.contains("request.tools.isNotEmpty() && request.model.supportsTools"))
        assertTrue(gemini.contains("if (request.tools.isNotEmpty())"))
        assertFalse(gemini.contains("request.tools.isNotEmpty() && request.model.supportsTools"))
        assertFalse(oauth.contains("if (request.model.supportsTools) request.tools.forEach"))
    }
}
