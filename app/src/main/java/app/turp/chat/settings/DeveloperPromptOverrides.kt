package app.turp.chat.settings

import app.turp.chat.data.MessageRole
import app.turp.chat.provider.ChatRequest
import app.turp.chat.provider.InputMessage
import app.turp.chat.provider.effectiveProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

const val DEVELOPER_PROMPT_DEFAULT_TOKEN = "{{default}}"

enum class DeveloperPromptKey(
    val id: String,
    val title: String,
    val description: String,
) {
    CORE_PROMPT("core_prompt", "Core Turp prompt", "The built-in Turp identity and general behavior prompt."),
    CUSTOM_PROFILE_OVERRIDE("custom_profile_override", "Custom profile override", "A user-selected OVERRIDE system-prompt profile when it replaces the Turp core."),
    PROFILE_APPEND("profile_append", "Additional prompt profile", "Wrapper around an APPEND system-prompt profile."),
    RESPONSE_STYLE("response_style", "Response style", "The Less emoji response-style layer when enabled."),
    RUNTIME_CONTEXT("runtime_context", "Runtime context", "Dynamic app version, clock, locale, enabled capabilities, and attachment availability."),
    TOOL_NATIVE("tool_native", "Native-tool policy", "Tool instructions when executable native functions are exposed."),
    TOOL_NATIVE_SUDO("tool_native_sudo", "Native-tool policy · Sudo", "Tool instructions when Sudo is active and native functions are exposed."),
    TOOL_NONE("tool_none", "No-tool policy", "Tool instructions when Turp exposes no executable native functions."),
    TOOL_NONE_SUDO("tool_none_sudo", "No-tool policy · Sudo", "Tool instructions when Sudo is active but no executable native functions are exposed."),
    DEEP_RESEARCH("deep_research", "Deep Research", "Planning, source, citation, and research-state protocol for Deep Research."),
    CITATION_POLICY("citation_policy", "Citation policy", "Website/file citation rules outside Deep Research."),
    ATTACHMENT_FILE_POLICY("attachment_file_policy", "Attachment & file policy", "Workspace attachment handling, generated-file delivery, and file-card rules."),
    PYTHON_PACKAGE_POLICY("python_package_policy", "Python package policy", "python-requirements behavior and package-install confirmation rules."),
    LINUX_RUNTIME_POLICY("linux_runtime_policy", "Linux runtime policy", "Linux execution, package management, timeout, and workspace rules."),
    RUN_REPAIR_POLICY("run_repair_policy", "Run repair policy", "Persisted run, workspace_read, apply_patch, and rerun behavior."),
    MEMORY_CONTEXT("memory_context", "Memory context", "Dynamic memory-disabled/empty/items context supplied to the model."),
    MEMORY_POLICY_AUTO("memory_policy_auto", "Memory policy · auto-save", "Rules used when automatic memory saving is enabled."),
    MEMORY_POLICY_MANUAL("memory_policy_manual", "Memory policy · manual", "Rules used when memory auto-save is disabled."),
    GENERATED_CONTENT("generated_content", "Generated-content capabilities", "Dynamic chart, diagram, widget, and generated-content contract instructions."),
    PRIMARY_SYSTEM_MESSAGE("primary_system_message", "Combined primary system message", "Final wrapper around the complete primary system message after all layers are assembled."),
    SUDO_LAYER("sudo_layer", "Sudo layer", "The separate high-priority Sudo system message."),
    COMPRESSED_CONTEXT("compressed_context", "Compressed-context wrapper", "Wrapper around older compressed conversation context."),
    CONTINUATION_TOOL_CONTEXT("continuation_tool_context", "Saved tool-context wrapper", "System wrapper used when resuming a saved assistant prefix with prior tool activity."),
    WORKING_CONTEXT("working_context", "Saved Working context", "Reasoning/tool-history appendix supplied back to the model with prior assistant turns."),
    OUTPUT_CONTINUATION("output_continuation", "Automatic output continuation", "User-role continuation instruction inserted when a provider reaches its output limit."),
    TOOL_RESULT_CONTEXT("tool_result_context", "Tool-result context", "Untrusted-data wrapper prefixed to ordinary external tool results."),
    TRUSTED_COMPILER_RESULT_CONTEXT("trusted_compiler_result_context", "Compiler-result context", "Trusted wrapper prefixed to Turp compiler tool results."),
    HISTORICAL_SYSTEM_EVENT("historical_system_event", "Stored system events", "Wrapper around system-role events already stored in chat history."),
    RESEARCH_INITIAL("research_initial", "Research-state initial instruction", "System instruction inserted when Deep Research has no valid state block yet."),
    RESEARCH_UPDATE("research_update", "Research-state update", "System instruction used to request an updated research state after new evidence."),
    RESEARCH_REPAIR("research_repair", "Research-state repair", "System instruction after an invalid research-state block."),
    RESEARCH_RECORDED("research_recorded", "Research-state recorded", "System instruction after Turp records model-reported research state."),
    RESEARCH_FINAL("research_final", "Research-state finalization", "System instruction used to request terminal research state."),
    TOOL_BUDGET_FINALIZATION("tool_budget_finalization", "Tool-budget finalization", "System instruction used when the model exhausts Turp's tool-round budget."),
    DEEPSEEK_TOOL_GUARD("deepseek_tool_guard", "DeepSeek structured-tool guard", "Provider-stage system instruction requiring native structured tool calls."),
    DEEPSEEK_TOOL_CORRECTION("deepseek_tool_correction", "DeepSeek tool-call correction", "Provider-stage retry system instruction after a tool call was serialized into text."),
    TOOL_DISABLED_PROTOCOL_CORRECTION("tool_disabled_protocol_correction", "Disabled-tool protocol correction", "Provider-stage retry instruction used during finalization when textual tool protocol leaked into content."),
    RESEARCH_TOOL_RESULT_REMINDER("research_tool_result_reminder", "Research tool-result reminder", "Instruction appended to tool results during Deep Research."),
    AUX_GENERATED_REPAIR("aux_generated_repair", "Auxiliary · generated-content repair", "System prompt for the generated-content repair model call."),
    AUX_WIDGET_SECURITY("aux_widget_security", "Auxiliary · widget security", "System prompt for the optional widget-security second opinion."),
    AUX_PACKAGE_REVIEW("aux_package_review", "Auxiliary · package review", "System prompt for model-based package approval review."),
    AUX_TITLE("aux_title", "Auxiliary · chat title", "System prompt for model-generated chat titles."),
    AUX_COMPRESSION("aux_compression", "Auxiliary · context compression", "System prompt for model-based context compression."),
    FINAL_SYSTEM_MESSAGE("final_system_message", "Every final system message", "Last catch-all applied to every system-role message before a provider request. Use this to verify or override anything not covered above."),
}

data class DeveloperPromptOverrides(
    val enabled: Boolean = false,
    val values: Map<String, String> = emptyMap(),
) {
    fun resolve(key: DeveloperPromptKey, defaultValue: String): String {
        if (!enabled) return defaultValue
        val template = values[key.id] ?: return defaultValue
        return template.replace(DEVELOPER_PROMPT_DEFAULT_TOKEN, defaultValue)
    }

    /**
     * Text shown by the direct developer editor. Legacy {{default}} wrappers are
     * expanded before display so the UI always edits concrete prompt text.
     */
    fun editorText(key: DeveloperPromptKey, defaultValue: String): String =
        values[key.id]?.replace(DEVELOPER_PROMPT_DEFAULT_TOKEN, defaultValue) ?: defaultValue

    fun editValue(key: DeveloperPromptKey): String =
        values[key.id] ?: DEVELOPER_PROMPT_DEFAULT_TOKEN

    fun hasOverride(key: DeveloperPromptKey): Boolean = values.containsKey(key.id)
}

data class DeveloperPromptComponentTrace(
    val key: String,
    val title: String,
    val defaultText: String,
    val effectiveText: String,
)

data class DeveloperPromptTrace(
    val conversationId: String,
    val capturedAt: Long,
    val systemMessages: List<String>,
    val components: Map<String, DeveloperPromptComponentTrace> = emptyMap(),
    val stage: String = "ASSEMBLED",
    val providerId: String = "",
    val profile: String = "",
    val protocol: String = "",
)

object DeveloperPromptTraceStore {
    private val _latest = MutableStateFlow<DeveloperPromptTrace?>(null)
    val latest: StateFlow<DeveloperPromptTrace?> = _latest.asStateFlow()

    fun record(
        conversationId: String,
        messages: List<InputMessage>,
        components: Map<String, DeveloperPromptComponentTrace> = emptyMap(),
    ) {
        val previous = _latest.value
        val mergedComponents = if (previous?.conversationId == conversationId) {
            previous.components + components
        } else {
            components
        }
        _latest.value = DeveloperPromptTrace(
            conversationId = conversationId,
            capturedAt = System.currentTimeMillis(),
            systemMessages = messages.filter { it.role == MessageRole.SYSTEM }.map(InputMessage::content),
            components = mergedComponents,
        )
    }

    fun recordComponent(
        conversationId: String,
        key: DeveloperPromptKey,
        defaultText: String,
        effectiveText: String,
    ) {
        val previous = _latest.value
        val base = if (previous?.conversationId == conversationId) {
            previous
        } else {
            DeveloperPromptTrace(
                conversationId = conversationId,
                capturedAt = System.currentTimeMillis(),
                systemMessages = emptyList(),
            )
        }
        _latest.value = base.copy(
            capturedAt = System.currentTimeMillis(),
            components = base.components + (
                key.id to DeveloperPromptComponentTrace(
                    key = key.id,
                    title = key.title,
                    defaultText = defaultText,
                    effectiveText = effectiveText,
                )
            ),
        )
    }

    fun recordProviderContext(request: ChatRequest, protocol: String) {
        val previous = _latest.value
        _latest.value = DeveloperPromptTrace(
            conversationId = request.sessionId,
            capturedAt = System.currentTimeMillis(),
            systemMessages = request.messages
                .filter { it.role == MessageRole.SYSTEM }
                .map(InputMessage::content),
            components = previous
                ?.takeIf { it.conversationId == request.sessionId }
                ?.components
                .orEmpty(),
            stage = "PROVIDER_BOUNDARY",
            providerId = request.provider.id,
            profile = request.provider.effectiveProfile.name,
            protocol = protocol,
        )
    }

    fun clear() {
        _latest.value = null
    }
}
