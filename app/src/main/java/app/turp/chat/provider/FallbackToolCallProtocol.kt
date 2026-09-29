package app.turp.chat.provider

import java.util.Locale
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private const val FALLBACK_TOOL_CALL_OPEN = "<turp-tool-call>"
private const val FALLBACK_TOOL_CALL_CLOSE = "</turp-tool-call>"

/**
 * Strict text fallback for providers/endpoints that reject native tool schemas.
 *
 * Keep this protocol stateless. In particular, avoid a singleton/static parser
 * object on Android: fallback is an optional compatibility path and must not be
 * able to fail a generation merely because a protocol holder failed to initialize.
 */
internal fun fallbackToolInstruction(tools: List<NativeToolDefinition>): String {
    if (tools.isEmpty()) return ""
    return buildString {
        appendLine("Turp fallback tool calling is enabled because native provider tool definitions are unavailable or were rejected.")
        appendLine("Native tool calling is preferred whenever it works. In this fallback mode, when you need exactly one Turp tool, output ONLY this envelope and nothing else:")
        appendLine(FALLBACK_TOOL_CALL_OPEN)
        appendLine("""{"name":"TOOL_NAME","arguments":{}}""")
        appendLine(FALLBACK_TOOL_CALL_CLOSE)
        appendLine("The whole response must be exactly one envelope. Do not wrap it in Markdown, add prose, emit multiple calls, or put the envelope inside reasoning.")
        appendLine("If no tool is needed, answer normally and do not output any turp-tool-call tag.")
        appendLine("Only these tools are executable for this request:")
        tools.forEach { tool ->
            append("- ").append(tool.name).append(": ").appendLine(tool.description)
            append("  arguments schema: ").appendLine(tool.parametersJson)
        }
    }.trim()
}

internal fun parseFallbackToolCallExact(
    text: String,
    allowedToolNames: Set<String>,
): NativeToolCall? {
    val trimmed = text.trim()
    if (!trimmed.startsWith(FALLBACK_TOOL_CALL_OPEN) ||
        !trimmed.endsWith(FALLBACK_TOOL_CALL_CLOSE)
    ) return null

    val payload = trimmed
        .removePrefix(FALLBACK_TOOL_CALL_OPEN)
        .removeSuffix(FALLBACK_TOOL_CALL_CLOSE)
        .trim()
    if (payload.isBlank()) return null

    val root = runCatching { ProviderJson.parseToJsonElement(payload).jsonObject }.getOrNull()
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

internal fun fallbackToolCallMessage(call: NativeToolCall): String = buildString {
    appendLine(FALLBACK_TOOL_CALL_OPEN)
    append(
        buildJsonObject {
            put("name", JsonPrimitive(call.name))
            put(
                "arguments",
                runCatching { ProviderJson.parseToJsonElement(call.argumentsJson).jsonObject }
                    .getOrElse { JsonObject(emptyMap()) },
            )
        }.toString(),
    )
    appendLine()
    append(FALLBACK_TOOL_CALL_CLOSE)
}

internal fun fallbackToolResultMessage(result: NativeToolResult): String {
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

internal fun containsFallbackToolEnvelopeHint(text: String): Boolean =
    text.contains(FALLBACK_TOOL_CALL_OPEN, ignoreCase = true) ||
        text.contains(FALLBACK_TOOL_CALL_CLOSE, ignoreCase = true)
