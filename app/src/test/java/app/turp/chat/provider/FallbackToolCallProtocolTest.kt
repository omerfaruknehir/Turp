package app.turp.chat.provider

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FallbackToolCallProtocolTest {
    private val allowed = setOf("web_search", "python")

    @Test
    fun acceptsOnlyExactWholeResponseEnvelopeForAllowedTool() {
        val call = FallbackToolCallProtocol.parseExact(
            """
            <turp-tool-call>
            {"name":"web_search","arguments":{"query":"Turp Android"}}
            </turp-tool-call>
            """.trimIndent(),
            allowed,
        )

        assertNotNull(call)
        assertEquals("web_search", call?.name)
        assertEquals("""{"query":"Turp Android"}""", call?.argumentsJson)
    }

    @Test
    fun rejectsEnvelopeEmbeddedInOrdinaryProse() {
        assertNull(
            FallbackToolCallProtocol.parseExact(
                """I'll search now. <turp-tool-call>{"name":"web_search","arguments":{"query":"x"}}</turp-tool-call>""",
                allowed,
            ),
        )
    }

    @Test
    fun rejectsUnknownToolExtraFieldsAndNonObjectArguments() {
        assertNull(
            FallbackToolCallProtocol.parseExact(
                """<turp-tool-call>{"name":"delete_everything","arguments":{}}</turp-tool-call>""",
                allowed,
            ),
        )
        assertNull(
            FallbackToolCallProtocol.parseExact(
                """<turp-tool-call>{"name":"web_search","arguments":{},"extra":true}</turp-tool-call>""",
                allowed,
            ),
        )
        assertNull(
            FallbackToolCallProtocol.parseExact(
                """<turp-tool-call>{"name":"web_search","arguments":"nope"}</turp-tool-call>""",
                allowed,
            ),
        )
    }

    @Test
    fun canonicalCallAndResultMessagesUseDedicatedEnvelope() {
        val call = NativeToolCall("c1", "python", """{"code":"print(42)"}""")
        val callMessage = FallbackToolCallProtocol.callMessage(call)
        assertNotNull(FallbackToolCallProtocol.parseExact(callMessage, setOf("python")))

        val result = FallbackToolCallProtocol.resultMessage(
            NativeToolResult("c1", "python", "42", isError = false),
        )
        assertTrue(result.contains("<turp-tool-result>"))
        assertTrue(result.contains("\"output\":\"42\""))
        assertFalse(result.contains("<turp-tool-call>"))
    }

    @Test
    fun instructionListsOnlyExposedRequestTools() {
        val instruction = FallbackToolCallProtocol.instruction(
            listOf(
                NativeToolDefinition(
                    name = "web_search",
                    description = "Search",
                    parametersJson = """{"type":"object","properties":{"query":{"type":"string"}}}""",
                ),
            ),
        )
        assertTrue(instruction.contains("- web_search: Search"))
        assertTrue(instruction.contains("<turp-tool-call>"))
        assertFalse(instruction.contains("python"))
    }
}
