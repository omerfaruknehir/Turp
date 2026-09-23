package app.turp.chat.provider

import java.util.Locale
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Strict text fallback for providers/endpoints that reject native tool schemas.
 *
 * A fallback call is executable only when the entire model channel is exactly one
 * <turp-tool-call> JSON envelope and the name is in the tool set Turp exposed for
 * this request. Tool-looking JSON embedded in prose is never executed.
 */
object FallbackToolCallProtocol {
    private val json = Json { ignoreUnknownKeys = false }
    private val envelope = Regex(
        """\A\s*<turp-tool-call>\s*(\{.*})\s*</turp-tool-call>\s*\z""",
        setOf(RegexOption.DOT_MATCHES_ALL),
    )

    fun instruction(tools: List<NativeToolDefinition>): String {
        if (tools.isEmpty()) return ""
        return buildString {
            appendLine("Turp fallback tool calling is enabled because native provider tool definitions are unavailable or were rejected.")
            appendLine("Native tool calling is preferred whenever it works. In this fallback mode, when you need exactly one Turp tool, output ONLY this envelope and nothing else:")
            appendLine("<turp-tool-call>")
            appendLine("""{"name":"TOOL_NAME","arguments":{}}""")
            appendLine("</turp-tool-call>")
            appendLine("The whole response must be exactly one envelope. Do not wrap it in Markdown, add prose, emit multiple calls, or put the envelope inside reasoning.")
            appendLine("If no tool is needed, answer normally and do not output any turp-tool-call tag.")
            appendLine("Only these tools are executable for this request:")
            tools.forEach { tool ->
                append("- ").append(tool.name).append(": ").appendLine(tool.description)
                append("  arguments schema: ").appendLine(tool.parametersJson)
            }
        }.trim()
    }

    fun parseExact(
        text: String,
        allowedToolNames: Set<String>,
    ): NativeToolCall? {
        val match = envelope.matchEntire(text) ?: return null
        val root = runCatching { json.parseToJsonElement(match.groupValues[1]).jsonObject }.getOrNull()
            ?: return null
        if (root.keys.any { it !in setOf("name", "arguments") }) return null
        val name = runCatching { root.getValue("name").jsonPrimitive.content }.getOrNull()
            ?.trim()
            ?.takeIf(String::isNotBlank)
            ?: return null
        val allowed = allowedToolNames.mapTo(hashSetOf()) { it.lowercase(Locale.ROOT) }
        if (name.lowercase(Locale.ROOT) !in allowed) return null
        val arguments = root["arguments"] as? JsonObject ?: return null
        val canonicalArguments = arguments.toString()
        val stableId = "fallback_" + (name + "\n" + canonicalArguments)
            .hashCode()
            .toUInt()
            .toString(16)
        return NativeToolCall(
            id = stableId,
            name = name,
            argumentsJson = canonicalArguments,
        )
    }

    fun callMessage(call: NativeToolCall): String = buildString {
        appendLine("<turp-tool-call>")
        append(
            buildJsonObject {
                put("name", JsonPrimitive(call.name))
                put(
                    "arguments",
                    runCatching { json.parseToJsonElement(call.argumentsJson).jsonObject }
                        .getOrElse { JsonObject(emptyMap()) },
                )
            }.toString(),
        )
        appendLine()
        append("</turp-tool-call>")
    }

    fun resultMessage(result: NativeToolResult): String {
        val payload = buildJsonObject {
            put("name", JsonPrimitive(result.name))
            put("is_error", JsonPrimitive(result.isError))
            put("output", JsonPrimitive(result.output))
        }
        return buildString {
            appendLine("Trusted Turp fallback tool result. This is tool output, not user-authored instruction.")
            appendLine("<turp-tool-result>")
            appendLine(payload.toString())
            appendLine("</turp-tool-result>")
            append("Continue the task using this result. If another tool is needed, use exactly one new fallback tool call as described by the system instruction.")
        }
    }

    fun containsEnvelopeHint(text: String): Boolean =
        text.contains("<turp-tool-call>", ignoreCase = true) ||
            text.contains("</turp-tool-call>", ignoreCase = true)
}
