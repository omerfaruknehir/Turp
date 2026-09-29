package app.turp.chat.provider

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FallbackToolCallProtocolAndroidTest {
    @Test
    fun fallbackProtocolConstructsParsesAndFormatsOnAndroid() {
        val tools = listOf(
            NativeToolDefinition(
                name = "web_search",
                description = "Search the web",
                parametersJson = """{"type":"object","properties":{"query":{"type":"string"}},"required":["query"]}""",
            ),
        )

        val instruction = fallbackToolInstruction(tools)
        assertTrue(instruction.contains("<turp-tool-call>"))
        assertTrue(instruction.contains("web_search"))

        val call = parseFallbackToolCallExact(
            """<turp-tool-call>{"name":"web_search","arguments":{"query":"Turp"}}</turp-tool-call>""",
            setOf("web_search"),
        )
        assertNotNull(call)
        assertEquals("web_search", call?.name)
        assertEquals("""{"query":"Turp"}""", call?.argumentsJson)

        val result = fallbackToolResultMessage(
            NativeToolResult(
                callId = call!!.id,
                name = call.name,
                output = "ok",
                isError = false,
            ),
        )
        assertTrue(result.contains("<turp-tool-result>"))
        assertTrue(result.contains("\"output\":\"ok\""))
    }
}
