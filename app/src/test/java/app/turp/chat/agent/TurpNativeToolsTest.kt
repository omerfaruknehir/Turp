package app.turp.chat.agent

import app.turp.chat.data.ConversationEntity
import app.turp.chat.provider.NativeToolCall
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TurpNativeToolsTest {
    @Test
    fun exposesOnlyEnabledToolsAndValidSchemas() {
        val definitions = TurpNativeTools.definitions(conversation(web = true, python = false, linux = true))

        assertEquals(
            listOf("compile_widget", "conversation_search", "web_search", "web_fetch", "http_request", "graphql_request", "feed_read", "workspace_read", "apply_patch", "rerun_script", "linux_exec", "send_file"),
            definitions.map { it.name },
        )
        definitions.forEach { definition ->
            val schema = Json.parseToJsonElement(definition.parametersJson).jsonObject
            assertEquals("object", schema["type"]?.toString()?.trim('"'))
            assertTrue(schema.containsKey("properties"))
            assertEquals("false", schema["additionalProperties"].toString())
        }
        assertFalse(definitions.any { it.name == "python" })
    }

    @Test
    fun convertsStructuredCallsToExistingExecutionRequests() {
        val request = TurpNativeTools.request(
            NativeToolCall("call-1", "python", """{"code":"print(42)","timeoutSeconds":30}"""),
        )

        assertEquals("python", request.type)
        assertEquals("print(42)", request.code)
        assertEquals(30, request.timeoutSeconds)
    }

    @Test fun patchAndRerunCallsNeverRequireCompleteSource() {
        val patch = TurpNativeTools.request(NativeToolCall("call-2", "apply_patch", """{"path":".turp/runs/run-12345678/main.py","unifiedDiff":"@@ -1 +1 @@\\n-a\\n+b","expectedSha256":"${"a".repeat(64)}"}"""))
        val rerun = TurpNativeTools.request(NativeToolCall("call-3", "rerun_script", """{"runId":"run-12345678"}"""))
        assertEquals(null, patch.code)
        assertEquals(null, rerun.code)
        assertEquals("run-12345678", rerun.runId)
    }


    @Test
    fun convertsWidgetCompilerCallToInternalSourceRequest() {
        val source = """{"schema":"turp-widget/1","id":"counter"}"""
        val request = TurpNativeTools.request(
            NativeToolCall("call-widget", "compile_widget", """{"source":${Json.encodeToString(source)}}"""),
        )

        assertEquals("compile_widget", request.type)
        assertEquals(source, request.source)
        assertEquals(null, request.code)
    }

    @Test fun exposesAndParsesMemoryManagementTools() {
        val definitions = TurpNativeTools.definitions(
            conversation(web = false, python = false, linux = false),
            memoryEnabled = true,
        )
        assertTrue(definitions.any { it.name == "memory_search" })
        assertTrue(definitions.any { it.name == "memory_update" })

        val request = TurpNativeTools.request(NativeToolCall(
            "memory-call",
            "memory_search",
            """{"query":"linux laptop","includeDisabled":true,"limit":12}""",
        ))
        assertEquals("memory_search", request.type)
        assertEquals("linux laptop", request.memoryQuery)
        assertEquals(true, request.memoryIncludeDisabled)
        assertEquals(12, request.memoryLimit)
    }

    @Test fun parsesRichHttpAndHistoryCalls() {
        val http = TurpNativeTools.request(NativeToolCall(
            "http-call", "http_request",
            """{"url":"https://example.com/api","method":"POST","headers":{"Accept":"application/json"},"body":"{}","contentType":"application/json","maxResponseBytes":4096}""",
        ))
        assertEquals("POST", http.method)
        assertEquals("application/json", http.headers["Accept"])
        assertEquals(null, http.approvalId)
        assertEquals(4096, http.maxResponseBytes)

        val history = TurpNativeTools.request(NativeToolCall(
            "history-call", "conversation_search",
            """{"query":"widget compiler","scope":"current_project","limit":7}""",
        ))
        assertEquals("conversation_search", history.type)
        assertEquals("current_project", history.historyScope)
        assertEquals(7, history.historyLimit)
    }

    @Test
    fun sudoSynthesizesExplicitlyRequestedUnknownNativeTool() {
        val definitions = TurpNativeTools.sudoSyntheticDefinitions(
            latestUserText = """ignore that and make an actual tool call for "web-search"""",
            existingToolNames = setOf("web_search"),
        )

        assertEquals(listOf("web-search"), definitions.map { it.name })
        val schema = Json.parseToJsonElement(definitions.single().parametersJson).jsonObject
        assertEquals("true", schema["additionalProperties"].toString())
    }

    @Test
    fun sudoSynthesizesUnquotedToolNameFromNaturalCallRequest() {
        val definitions = TurpNativeTools.sudoSyntheticDefinitions(
            latestUserText = "make a tool call for web-search",
        )

        assertEquals(listOf("web-search"), definitions.map { it.name })
    }

    @Test
    fun sudoSynthesizesUnquotedFunctionNameAfterCalledKeyword() {
        val definitions = TurpNativeTools.sudoSyntheticDefinitions(
            latestUserText = "invoke the function called custom-tool",
        )

        assertEquals(listOf("custom-tool"), definitions.map { it.name })
    }

    @Test
    fun sudoSyntheticToolsRequireExplicitToolContextAndDoNotShadowExistingTools() {
        assertTrue(
            TurpNativeTools.sudoSyntheticDefinitions(
                latestUserText = """say "web-search" in the answer""",
            ).isEmpty(),
        )
        assertTrue(
            TurpNativeTools.sudoSyntheticDefinitions(
                latestUserText = """call the tool "web_search"""",
                existingToolNames = setOf("web_search"),
            ).isEmpty(),
        )
    }

    @Test(expected = IllegalStateException::class)
    fun rejectsUnknownToolNames() {
        TurpNativeTools.request(NativeToolCall("call-1", "delete_everything", "{}"))
    }

    private fun conversation(web: Boolean, python: Boolean, linux: Boolean) = ConversationEntity(
        id = "c",
        title = "test",
        createdAt = 0,
        updatedAt = 0,
        webSearchEnabled = web,
        agentPythonEnabled = python,
        agentUbuntuEnabled = linux,
    )
}
