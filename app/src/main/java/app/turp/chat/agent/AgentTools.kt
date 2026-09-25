package app.turp.chat.agent

import android.text.Html
import app.turp.chat.data.ConversationEntity
import app.turp.chat.chat.ChatRepository
import app.turp.chat.sandbox.PythonSandbox
import app.turp.chat.sandbox.UbuntuRuntime
import app.turp.chat.sandbox.RunRecordStore
import app.turp.chat.sandbox.ScriptRuntime
import app.turp.chat.sandbox.ExecutionProgress
import app.turp.chat.generated.GeneratedBlockCompiler
import app.turp.chat.generated.GeneratedBlockType
import app.turp.chat.generated.WidgetCompilerToolProtocol
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Dns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import java.net.URLDecoder
import java.net.URI
import java.net.InetAddress
import java.io.File
import java.util.UUID
import java.util.concurrent.TimeUnit

@Serializable
data class AgentToolRequest(
    val type: String,
    val query: String? = null,
    val code: String? = null,
    val source: String? = null,
    val url: String? = null,
    val method: String? = null,
    val headers: Map<String, String> = emptyMap(),
    val body: String? = null,
    val contentType: String? = null,
    val approvalId: String? = null,
    val maxResponseBytes: Int? = null,
    val graphqlQuery: String? = null,
    val graphqlVariablesJson: String? = null,
    val graphqlOperationName: String? = null,
    val feedLimit: Int? = null,
    val historyScope: String? = null,
    val historyLimit: Int? = null,
    val includeCurrentConversation: Boolean? = null,
    val command: String? = null,
    val path: String? = null,
    val caption: String? = null,
    val timeoutSeconds: Int? = null,
    val startLine: Int? = null,
    val endLine: Int? = null,
    val maxBytes: Int? = null,
    val unifiedDiff: String? = null,
    val expectedSha256: String? = null,
    val runId: String? = null,
    val memoryId: String? = null,
    val memoryText: String? = null,
    val memoryCategory: String? = null,
    val memoryQuery: String? = null,
    val memoryIncludeDisabled: Boolean? = null,
    val memoryLimit: Int? = null,
    val args: List<String> = emptyList(),
)

@Serializable
data class ToolTraceEvent(
    val id: String = UUID.randomUUID().toString(),
    val type: String,
    val label: String,
    val status: String,
    val input: String = "",
    val output: String = "",
    val providerCallId: String = "",
    val argumentsJson: String = "",
    val startedAt: Long,
    val finishedAt: Long? = null,
)

@Serializable
data class MessageTimelineEvent(
    val id: String = UUID.randomUUID().toString(),
    val kind: String,
    val content: String = "",
    val label: String = "",
    val status: String = "complete",
    val input: String = "",
    val output: String = "",
    val providerCallId: String = "",
    val argumentsJson: String = "",
    val startedAt: Long,
    val finishedAt: Long? = null,
    /**
     * Text/reasoning events created by current Turp builds reference the
     * aggregate message field instead of duplicating a growing string inside
     * timelineJson. A null sourceEnd marks the currently streaming segment.
     */
    val sourceStart: Int = -1,
    val sourceEnd: Int? = null,
)

fun materializeTimelineContent(
    events: List<MessageTimelineEvent>,
    content: String,
    reasoning: String,
): List<MessageTimelineEvent> {
    val materialized = events.mapIndexed { index, event ->
        if (event.content.isNotEmpty() || event.sourceStart < 0 || event.kind !in setOf("text", "reasoning")) {
            event
        } else {
            val source = if (event.kind == "reasoning") reasoning else content
            val start = event.sourceStart.coerceIn(0, source.length)
            val nextStart = events.asSequence()
                .drop(index + 1)
                .firstOrNull { it.kind == event.kind && it.sourceStart >= 0 }
                ?.sourceStart
            val end = (event.sourceEnd ?: nextStart ?: source.length).coerceIn(start, source.length)
            event.copy(content = source.substring(start, end))
        }
    }
    return coalesceStreamingTextFragments(materialized)
}

/**
 * Providers may emit reasoning and visible text in the same SSE event. Older
 * timeline code treated every field switch as a new visual block, so a provider
 * which repeated both fields produced one Markdown block per token. Within each
 * tool-free run, text and reasoning are aggregate streams: keep one event per
 * kind and concatenate the exact fragments without inserting whitespace.
 */
internal fun coalesceStreamingTextFragments(events: List<MessageTimelineEvent>): List<MessageTimelineEvent> {
    if (events.size < 2) return events
    val result = mutableListOf<MessageTimelineEvent>()
    val streamRun = mutableListOf<MessageTimelineEvent>()

    fun flushStreamRun() {
        if (streamRun.isEmpty()) return
        val byKind = linkedMapOf<String, MutableList<MessageTimelineEvent>>()
        streamRun.forEach { event -> byKind.getOrPut(event.kind) { mutableListOf() } += event }
        byKind.values.forEach { fragments ->
            val first = fragments.first()
            result += first.copy(
                content = buildString { fragments.forEach { append(it.content) } },
                finishedAt = if (fragments.any { it.finishedAt == null }) null else fragments.maxOfOrNull { it.finishedAt ?: it.startedAt },
                sourceStart = -1,
                sourceEnd = null,
            )
        }
        streamRun.clear()
    }

    events.forEach { event ->
        if (event.kind in setOf("text", "reasoning")) {
            streamRun += event
        } else {
            flushStreamRun()
            result += event
        }
    }
    flushStreamRun()
    return result
}

data class TimelineRun(val working: Boolean, val events: List<MessageTimelineEvent>)

fun groupOrderedTimeline(events: List<MessageTimelineEvent>): List<TimelineRun> {
    if (events.isEmpty()) return emptyList()
    val result = mutableListOf<TimelineRun>()
    var currentWorking = events.first().kind !in setOf("text", "file")
    var current = mutableListOf<MessageTimelineEvent>()
    events.forEach { event ->
        val working = event.kind !in setOf("text", "file")
        if (current.isNotEmpty() && working != currentWorking) {
            result += TimelineRun(currentWorking, current.toList())
            current = mutableListOf()
        }
        currentWorking = working
        current += event
    }
    if (current.isNotEmpty()) result += TimelineRun(currentWorking, current.toList())
    return result
}


data class AgentToolOutcome(
    val output: String,
    val files: List<String> = emptyList(),
    val isError: Boolean = false,
)

@Serializable
private data class SentFileResult(val path: String, val name: String, val sizeBytes: Long, val caption: String)

class AgentTools internal constructor(
    private val python: PythonSandbox,
    private val ubuntu: UbuntuRuntime,
    private val repository: ChatRepository,
    private val generatedBlockCompiler: GeneratedBlockCompiler,
    val runRecords: RunRecordStore = RunRecordStore(ubuntu::workspace),
    private val webSearchClient: WebSearchClient,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .dns(PublicOnlyDns)
        .build(),
    private val httpWriteApprovals: HttpWriteApprovalGuard = HttpWriteApprovalGuard(),
) {
    private val json = Json { encodeDefaults = true }

    suspend fun execute(
        conversationId: String,
        request: AgentToolRequest,
        onProgress: suspend (ExecutionProgress) -> Unit = {},
    ): AgentToolOutcome {
        // Permissions are intentionally re-read immediately before every side effect.
        val conversation = requireNotNull(repository.conversationNow(conversationId)) { "Conversation no longer exists" }
        return when (request.type.lowercase()) {
        "compile_widget", "widget_compile" -> {
            val source = requireNotNull(request.source) { "Widget source is missing" }
            val compilation = generatedBlockCompiler.compile(GeneratedBlockType.HOME_WIDGET, source)
            val result = WidgetCompilerToolProtocol.result(source, compilation)
            AgentToolOutcome(json.encodeToString(result), isError = !result.success)
        }
        "conversation_search" -> {
            val query = requireNotNull(request.query) { "Conversation search query is missing" }.trim()
            require(query.isNotBlank()) { "Conversation search query is empty" }
            val requestedScope = request.historyScope?.trim()?.takeIf(String::isNotBlank)?.lowercase()
            val projectId = when {
                requestedScope == "all" -> null
                requestedScope == "current_project" -> requireNotNull(conversation.projectId) { "The current conversation is not in a project" }
                requestedScope != null -> error("history scope must be all or current_project")
                conversation.projectId != null -> conversation.projectId
                else -> error("Outside a project, conversation_search requires explicit scope=all")
            }
            val hits = repository.searchHistory(
                text = query,
                projectId = projectId,
                excludeConversationId = conversation.id.takeUnless { request.includeCurrentConversation == true },
                limit = (request.historyLimit ?: 20).coerceIn(1, 50),
            ).map { hit ->
                ConversationSearchToolItem(hit.nodeId, hit.conversationId, hit.conversationTitle, hit.snippet, hit.rank)
            }
            AgentToolOutcome(json.encodeToString(hits))
        }
        "web_search", "search" -> {
            check(conversation.webSearchEnabled) { "Web search is disabled for this conversation." }
            AgentToolOutcome(webSearchClient.search(requireNotNull(request.query) { "Search query is missing" }))
        }
        "web_fetch", "fetch" -> {
            check(conversation.webSearchEnabled) { "Web access is disabled for this conversation." }
            AgentToolOutcome(fetch(requireNotNull(request.url) { "Fetch URL is missing" }))
        }
        "http_request" -> {
            check(conversation.webSearchEnabled) { "Web access is disabled for this conversation." }
            AgentToolOutcome(httpRequest(conversation, request))
        }
        "graphql_request" -> {
            check(conversation.webSearchEnabled) { "Web access is disabled for this conversation." }
            AgentToolOutcome(graphqlRequest(conversation, request))
        }
        "feed_read" -> {
            check(conversation.webSearchEnabled) { "Web access is disabled for this conversation." }
            AgentToolOutcome(feedRead(request))
        }
        "python", "python_exec" -> {
            check(conversation.agentPythonEnabled) { "Agent Python is disabled for this conversation." }
            val code = requireNotNull(request.code) { "Python code is missing" }
            val timeout = (request.timeoutSeconds ?: DEFAULT_PYTHON_SECONDS).coerceIn(1, 600)
            var metadata = runRecords.create(
                conversation.id, ScriptRuntime.PYTHON, code, "python", emptyList(), timeout,
                mapOf("python" to "bundled 3.12", "packages" to ".packages", "executionMode" to "embedded app process"),
            )
            metadata = runRecords.markStarted(metadata, timeout, emptyList())
            val result = executeStored(metadata, emptyList(), timeout, onProgress)
            AgentToolOutcome(json.encodeToString(result), isError = result.exitCode != 0 || result.timedOut || result.cancelled)
        }
        "ubuntu", "ubuntu_exec", "linux", "linux_exec", "shell" -> {
            check(conversation.agentUbuntuEnabled) { "Agent Linux tools are disabled for this conversation." }
            val command = (request.command ?: request.code).orEmpty().trim()
            require(command.isNotBlank()) { "Linux command is missing" }
            require(!PACKAGE_COMMAND.containsMatchIn(command)) {
                "Package-manager commands require a visible ubuntu-packages request and approval."
            }
            val timeout = (request.timeoutSeconds ?: DEFAULT_LINUX_SECONDS).coerceIn(1, 900)
            var metadata = runRecords.create(
                conversation.id, ScriptRuntime.LINUX, command, "sh", emptyList(), timeout,
                mapOf("distribution" to ubuntu.distribution.value.displayName, "executionMode" to "PRoot root"),
            )
            metadata = runRecords.markStarted(metadata, timeout, emptyList())
            val result = executeStored(metadata, emptyList(), timeout, onProgress)
            AgentToolOutcome(json.encodeToString(result), isError = result.exitCode != 0 || result.timedOut)
        }
        "workspace_read" -> {
            check(conversation.agentPythonEnabled || conversation.agentUbuntuEnabled) { "Workspace tools are disabled for this conversation." }
            AgentToolOutcome(json.encodeToString(runRecords.readWorkspace(
                conversation.id,
                requireNotNull(request.path) { "Workspace path is missing" },
                request.startLine,
                request.endLine,
                request.maxBytes,
            )))
        }
        "apply_patch" -> {
            check(conversation.agentPythonEnabled || conversation.agentUbuntuEnabled) { "Workspace tools are disabled for this conversation." }
            AgentToolOutcome(json.encodeToString(runRecords.applyPatch(
                conversation.id,
                requireNotNull(request.path) { "Workspace path is missing" },
                requireNotNull(request.unifiedDiff) { "unifiedDiff is missing" },
                requireNotNull(request.expectedSha256) { "expectedSha256 is missing" },
            )))
        }
        "rerun_script" -> {
            check(conversation.agentPythonEnabled || conversation.agentUbuntuEnabled) { "Workspace tools are disabled for this conversation." }
            var metadata = runRecords.load(conversation.id, requireNotNull(request.runId) { "runId is missing" })
            if (metadata.runtime == ScriptRuntime.PYTHON) check(conversation.agentPythonEnabled) { "Agent Python is disabled for this conversation." }
            if (metadata.runtime == ScriptRuntime.LINUX) check(conversation.agentUbuntuEnabled) { "Agent Linux tools are disabled for this conversation." }
            val maximum = if (metadata.runtime == ScriptRuntime.PYTHON) 600 else 900
            val timeout = (request.timeoutSeconds ?: metadata.timeoutSeconds).coerceIn(1, maximum)
            val args = request.args.ifEmpty { metadata.originalArgs }
            metadata = runRecords.markStarted(metadata, timeout, args)
            val result = executeStored(metadata, args, timeout, onProgress)
            AgentToolOutcome(json.encodeToString(result), isError = result.exitCode != 0 || result.timedOut || result.cancelled)
        }
        "memory_save" -> {
            val settings = repository.automationSettingsNow()
            check(settings.memoryEnabled) { "Memory is disabled in Turp settings." }
            val result = repository.saveMemoryManaged(
                content = requireNotNull(request.memoryText) { "Memory text is missing" },
                category = request.memoryCategory.orEmpty().ifBlank { "general" },
                sourceConversationId = conversation.id,
            )
            AgentToolOutcome(json.encodeToString(MemorySaveToolResult(
                saved = true,
                created = result.created,
                updated = !result.created,
                id = result.memory.id,
                category = result.memory.category,
                content = result.memory.content,
                mergedMemoryId = result.mergedMemoryId,
            )))
        }
        "memory_list", "memory_search" -> {
            val settings = repository.automationSettingsNow()
            check(settings.memoryEnabled) { "Memory is disabled in Turp settings." }
            val query = request.memoryQuery.orEmpty().trim()
            if (request.type.equals("memory_search", ignoreCase = true)) {
                require(query.isNotBlank()) { "Memory search query is missing" }
            }
            val memories = repository.searchMemories(
                query = query,
                includeDisabled = request.memoryIncludeDisabled ?: false,
                limit = (request.memoryLimit ?: 100).coerceIn(1, 200),
            ).map { memory ->
                MemoryToolItem(
                    id = memory.id,
                    content = memory.content,
                    category = memory.category,
                    enabled = memory.enabled,
                    updatedAt = memory.updatedAt,
                )
            }
            AgentToolOutcome(json.encodeToString(memories))
        }
        "memory_update" -> {
            val settings = repository.automationSettingsNow()
            check(settings.memoryEnabled) { "Memory is disabled in Turp settings." }
            val result = repository.updateMemory(
                id = requireNotNull(request.memoryId) { "Memory id is missing" },
                content = requireNotNull(request.memoryText) { "Memory text is missing" },
                category = request.memoryCategory.orEmpty().ifBlank { "general" },
            )
            AgentToolOutcome(json.encodeToString(MemorySaveToolResult(
                saved = true,
                created = false,
                updated = true,
                id = result.memory.id,
                category = result.memory.category,
                content = result.memory.content,
                mergedMemoryId = result.mergedMemoryId,
            )))
        }
        "memory_forget" -> {
            val settings = repository.automationSettingsNow()
            check(settings.memoryEnabled) { "Memory is disabled in Turp settings." }
            val id = requireNotNull(request.memoryId) { "Memory id is missing" }
            AgentToolOutcome(json.encodeToString(MemoryForgetToolResult(
                forgotten = repository.deleteMemory(id),
                id = id,
            )))
        }
        "send_file", "file_send" -> {
            val relative = requireNotNull(request.path) { "File path is missing" }.trim().removePrefix("/workspace/")
            require(relative.isNotBlank() && !File(relative).isAbsolute) { "Use a path inside the conversation workspace" }
            val workspace = ubuntu.workspace(conversation.id).canonicalFile
            val source = File(workspace, relative).canonicalFile
            require(source.isFile && source.path.startsWith(workspace.path + File.separator)) {
                "The requested file does not exist in this conversation workspace"
            }
            AgentToolOutcome(
                json.encodeToString(SentFileResult(relative, source.name, source.length(), request.caption?.take(500).orEmpty())),
                listOf(relative),
            )
        }
        else -> error("Unknown Turp tool: ${request.type}")
        }
    }

    private suspend fun executeStored(
        metadata: app.turp.chat.sandbox.ScriptRunMetadata,
        args: List<String>,
        timeout: Int,
        onProgress: suspend (ExecutionProgress) -> Unit,
    ): app.turp.chat.sandbox.ScriptRunResult {
        val started = System.currentTimeMillis()
        return try {
            val raw = if (metadata.runtime == ScriptRuntime.PYTHON) {
                python.executeFile(metadata.conversationId, metadata.scriptPath, args, timeout).let { result ->
                    onProgress(ExecutionProgress(result.stdout.takeLast(12_000), result.stderr.takeLast(12_000), result.elapsedMs))
                    StoredExecution(
                        stdout = result.stdout,
                        stderr = result.stderr,
                        exitCode = result.exitCode,
                        files = result.files,
                        elapsedMs = result.elapsedMs,
                        timedOut = result.timedOut,
                        cancelled = result.cancelled,
                    )
                }
            } else {
                ubuntu.executeShellFile(metadata.conversationId, metadata.scriptPath, args, timeout, onProgress).let { result ->
                    StoredExecution(
                        stdout = result.stdout,
                        stderr = result.stderr,
                        exitCode = result.exitCode,
                        files = result.files,
                        elapsedMs = result.elapsedMs,
                        timedOut = result.timedOut,
                    )
                }
            }
            runRecords.finish(metadata, raw.stdout, raw.stderr, raw.exitCode, raw.timedOut, raw.cancelled, raw.elapsedMs, raw.files)
        } catch (cancelled: CancellationException) {
            runRecords.finish(
                metadata = metadata,
                stdout = "",
                stderr = "Execution cancelled; the process tree was terminated.",
                exitCode = 130,
                timedOut = false,
                cancelled = true,
                elapsedMs = System.currentTimeMillis() - started,
                changedFiles = emptyList(),
            )
            throw cancelled
        }
    }

    private data class StoredExecution(
        val stdout: String,
        val stderr: String,
        val exitCode: Int,
        val files: List<String>,
        val elapsedMs: Long,
        val timedOut: Boolean,
        val cancelled: Boolean = false,
    )

    private suspend fun search(rawQuery: String): String = withContext(Dispatchers.IO) {
        val query = rawQuery.trim().take(500)
        require(query.isNotBlank()) { "Search query is empty" }
        val url = "https://html.duckduckgo.com/html/".toHttpUrl().newBuilder()
            .addQueryParameter("q", query)
            .build()
        val request = Request.Builder().url(url)
            .header("User-Agent", "Mozilla/5.0 (Android) Turp/0.12.0")
            .header("Accept", "text/html")
            .build()
        client.newCall(request).execute().use { response ->
            check(response.isSuccessful) { "Search failed with HTTP ${response.code}" }
            val html = response.body?.readLimited(2_000_000).orEmpty()
            val results = parseDuckDuckGo(html).take(8)
            if (results.isEmpty()) "No search results were returned for: $query"
            else json.encodeToString(WebSearchResponse(query = query, engine = "DuckDuckGo", results = results))
        }
    }

    private suspend fun httpRequest(conversation: ConversationEntity, request: AgentToolRequest): String {
        val method = HttpToolPolicy.normalizeMethod(request.method)
        val headers = HttpToolPolicy.validateHeaders(request.headers)
        val body = HttpToolPolicy.validateRequest(method, request.body, request.contentType)
        if (HttpToolPolicy.requiresWriteApproval(method)) {
            approvalOrResult(
                conversation = conversation,
                request = request,
                method = method,
                headers = headers,
                body = body,
                contentType = request.contentType,
            )?.let { return it }
        }
        return json.encodeToString(executeHttp(
            rawUrl = requireNotNull(request.url) { "HTTP URL is missing" },
            requestedMethod = method,
            requestedHeaders = headers,
            body = body,
            contentType = request.contentType,
            maxResponseBytes = request.maxResponseBytes,
            hardResponseLimit = HttpToolPolicy.MAX_API_RESPONSE_BYTES,
        ))
    }

    private suspend fun graphqlRequest(conversation: ConversationEntity, request: AgentToolRequest): String {
        val query = requireNotNull(request.graphqlQuery) { "GraphQL query is missing" }.trim()
        require(query.isNotBlank()) { "GraphQL query is empty" }
        val mutation = Regex("(?im)^\\s*mutation\\b").containsMatchIn(query)
        val payload = buildJsonObject {
            put("query", JsonPrimitive(query))
            request.graphqlVariablesJson?.let { variables ->
                put("variables", json.parseToJsonElement(variables))
            }
            request.graphqlOperationName?.takeIf(String::isNotBlank)?.let { operation ->
                put("operationName", JsonPrimitive(operation))
            }
        }.toString()
        val headers = HttpToolPolicy.validateHeaders(request.headers + ("Accept" to "application/json"))
        if (mutation) {
            approvalOrResult(
                conversation = conversation,
                request = request,
                method = "POST",
                headers = headers,
                body = payload,
                contentType = "application/json; charset=utf-8",
            )?.let { return it }
        }
        return json.encodeToString(executeHttp(
            rawUrl = requireNotNull(request.url) { "GraphQL URL is missing" },
            requestedMethod = "POST",
            requestedHeaders = headers,
            body = payload,
            contentType = "application/json; charset=utf-8",
            maxResponseBytes = request.maxResponseBytes,
            hardResponseLimit = HttpToolPolicy.MAX_API_RESPONSE_BYTES,
            allowReadOnlyPost = !mutation,
        ))
    }

    private suspend fun approvalOrResult(
        conversation: ConversationEntity,
        request: AgentToolRequest,
        method: String,
        headers: Map<String, String>,
        body: String?,
        contentType: String?,
    ): String? {
        val latestUser = repository.recent(conversation.id, 20).firstOrNull { it.role == app.turp.chat.data.MessageRole.USER }
            ?: error("HTTP writes require a current user message")
        val targetUrl = requireNotNull(request.url) { "HTTP URL is missing" }
        val identity = HttpWriteRequestIdentity(
            method = method,
            url = targetUrl.trim(),
            headers = headers,
            body = body.orEmpty(),
            contentType = contentType.orEmpty(),
        )
        return when (val decision = httpWriteApprovals.authorize(
            conversationId = conversation.id,
            latestUserNodeId = latestUser.nodeId,
            latestUserText = latestUser.content,
            request = identity,
            approvalId = request.approvalId,
        )) {
            is HttpWriteApprovalDecision.Approved -> null
            is HttpWriteApprovalDecision.Required -> json.encodeToString(HttpWriteApprovalToolResult(
                status = "approval_required",
                approvalId = decision.approvalId,
                confirmationText = decision.confirmationText,
                method = method,
                url = targetUrl,
                bodySha256 = identity.bodySha256,
                expiresAt = decision.expiresAt,
                instruction = "Do not retry this write in the current turn. Ask the user to reply with confirmationText exactly, then repeat the identical request with approvalId.",
            ))
        }
    }

    private suspend fun feedRead(request: AgentToolRequest): String {
        val limit = (request.feedLimit ?: 20).coerceIn(1, 50)
        val response = executeHttp(
            rawUrl = requireNotNull(request.url) { "Feed URL is missing" },
            requestedMethod = "GET",
            requestedHeaders = mapOf("Accept" to "application/atom+xml,application/rss+xml,application/xml,text/xml;q=0.9,*/*;q=0.2"),
            body = null,
            contentType = null,
            maxResponseBytes = request.maxResponseBytes ?: HttpToolPolicy.MAX_FEED_RESPONSE_BYTES,
            hardResponseLimit = HttpToolPolicy.MAX_FEED_RESPONSE_BYTES,
        )
        return json.encodeToString(FeedParser.parse(response.body, response.url, limit))
    }

    private suspend fun executeHttp(
        rawUrl: String,
        requestedMethod: String?,
        requestedHeaders: Map<String, String>,
        body: String?,
        contentType: String?,
        maxResponseBytes: Int?,
        hardResponseLimit: Int,
        allowReadOnlyPost: Boolean = false,
    ): HttpToolResponse = withContext(Dispatchers.IO) {
        val method = HttpToolPolicy.normalizeMethod(requestedMethod)
        require(method in setOf("GET", "HEAD") || (allowReadOnlyPost && method == "POST") || HttpToolPolicy.requiresWriteApproval(method)) {
            "Unsupported HTTP execution mode"
        }
        val headers = HttpToolPolicy.validateHeaders(requestedHeaders)
        val activeBody = HttpToolPolicy.validateRequest(method, body, contentType)
        val responseLimit = HttpToolPolicy.responseLimit(maxResponseBytes, hardResponseLimit)
        var url = validatePublicUrl(rawUrl)

        repeat(4) { redirectCount ->
            val builder = Request.Builder().url(url)
                .header("User-Agent", "Mozilla/5.0 (Android) Turp/0.12.0")
            headers.forEach { (name, value) -> builder.header(name, value) }
            if (!contentType.isNullOrBlank() && headers.keys.none { it.equals("Content-Type", ignoreCase = true) }) {
                builder.header("Content-Type", contentType)
            }
            val mediaType = contentType?.toMediaTypeOrNull()
            val requestBody = when {
                method in setOf("GET", "HEAD") -> null
                activeBody != null -> activeBody.toRequestBody(mediaType)
                method in setOf("POST", "PUT", "PATCH") -> "".toRequestBody(mediaType)
                else -> null
            }
            builder.method(method, requestBody)

            client.newBuilder().followRedirects(false).build().newCall(builder.build()).execute().use { response ->
                if (response.code in 300..399) {
                    val location = response.header("Location") ?: error("Redirect has no Location header")
                    if (method !in setOf("GET", "HEAD")) {
                        error("Redirects are blocked for POST, PUT, PATCH, and DELETE API requests; call the final HTTPS endpoint directly")
                    }
                    val target = validatePublicUrl(response.request.url.resolve(location)?.toString() ?: location)
                    val crossOrigin = !sameOrigin(url, target)
                    val crossOriginSensitiveHeaders = headers.keys.any { !it.equals("Accept", ignoreCase = true) }
                    if (crossOrigin && crossOriginSensitiveHeaders) {
                        error("Cross-origin redirects are blocked for requests carrying custom headers")
                    }
                    url = target
                    return@repeat
                }

                val responseContentType = response.header("Content-Type").orEmpty()
                require(HttpToolPolicy.isTextualContentType(responseContentType)) {
                    "HTTP API tool supports textual, JSON, XML, and form responses; received ${responseContentType.ifBlank { "unknown binary content" }}"
                }
                val limited = if (method == "HEAD") LimitedResponseText("", false)
                    else response.body?.readLimitedWithTruncation(responseLimit.toLong()) ?: LimitedResponseText("", false)
                val safeHeaders = response.headers.names().asSequence()
                    .filterNot(HttpToolPolicy::isSensitiveResponseHeader)
                    .take(32)
                    .associateWith { name -> response.header(name).orEmpty().take(4_000) }
                return@withContext HttpToolResponse(
                    url = url,
                    method = method,
                    status = response.code,
                    contentType = responseContentType,
                    headers = safeHeaders,
                    body = limited.text,
                    truncated = limited.truncated,
                )
            }
            if (redirectCount == 3) error("Too many redirects")
        }
        error("Unable to complete HTTP request")
    }

    private fun sameOrigin(left: String, right: String): Boolean {
        val a = URI(left)
        val b = URI(right)
        fun port(uri: URI) = if (uri.port >= 0) uri.port else 443
        return a.scheme.equals(b.scheme, ignoreCase = true) &&
            a.host.equals(b.host, ignoreCase = true) && port(a) == port(b)
    }

    private suspend fun fetch(rawUrl: String): String = withContext(Dispatchers.IO) {
        var url = validatePublicUrl(rawUrl)
        repeat(4) { redirectCount ->
            val request = Request.Builder().url(url)
                .header("User-Agent", "Mozilla/5.0 (Android) Turp/0.12.0")
                .header("Accept", "text/html,text/plain,application/json;q=0.9,*/*;q=0.2")
                .build()
            client.newBuilder().followRedirects(false).build().newCall(request).execute().use { response ->
                if (response.code in 300..399) {
                    val location = response.header("Location") ?: error("Redirect has no Location header")
                    url = validatePublicUrl(response.request.url.resolve(location)?.toString() ?: location)
                    return@repeat
                }
                check(response.isSuccessful) { "Fetch failed with HTTP ${response.code}" }
                val contentType = response.header("Content-Type").orEmpty()
                val raw = response.body?.readLimited(2_000_000).orEmpty()
                val text = if ("html" in contentType || raw.contains("<html", ignoreCase = true)) plain(raw) else raw
                return@withContext json.encodeToString(WebFetchResponse(url, contentType, text.take(60_000)))
            }
            if (redirectCount == 3) error("Too many redirects")
        }
        error("Unable to fetch URL")
    }

    private fun validatePublicUrl(raw: String): String {
        val clean = raw.trim()
        require(clean.length <= 8_192) { "URL is too long" }
        val uri = URI(clean)
        require(uri.scheme == "https" && !uri.host.isNullOrBlank()) { "Only absolute HTTPS URLs can be fetched" }
        require(uri.userInfo.isNullOrBlank()) { "Credentials embedded in URLs are not allowed" }
        val addresses = InetAddress.getAllByName(uri.host)
        require(addresses.isNotEmpty() && addresses.none(PublicNetworkPolicy::isBlockedAddress)) {
            "Local and private network addresses are blocked from web fetch"
        }
        return uri.toString()
    }

    private fun parseDuckDuckGo(html: String): List<WebSearchResult> {
        val anchor = Regex("<a[^>]*class=\\\"[^\\\"]*result__a[^\\\"]*\\\"[^>]*href=\\\"([^\\\"]+)\\\"[^>]*>([\\s\\S]*?)</a>", RegexOption.IGNORE_CASE)
        val snippet = Regex("class=\\\"[^\\\"]*result__snippet[^\\\"]*\\\"[^>]*>([\\s\\S]*?)</(?:a|div)>", RegexOption.IGNORE_CASE)
        return anchor.findAll(html).map { match ->
            val windowEnd = minOf(html.length, match.range.last + 4_000)
            val nearby = html.substring(match.range.last + 1, windowEnd)
            WebSearchResult(
                title = plain(match.groupValues[2]),
                url = cleanUrl(match.groupValues[1]),
                snippet = snippet.find(nearby)?.groupValues?.get(1)?.let(::plain).orEmpty(),
            )
        }.filter { it.title.isNotBlank() && it.url.startsWith("http") }.distinctBy { it.url }.toList()
    }

    private fun cleanUrl(value: String): String {
        val decoded = Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY).toString()
        val target = Regex("[?&]uddg=([^&]+)").find(decoded)?.groupValues?.get(1)
        return if (target == null) decoded else runCatching { URLDecoder.decode(target, "UTF-8") }.getOrDefault(decoded)
    }

    private fun plain(value: String): String = Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY)
        .toString().replace(Regex("\\s+"), " ").trim()

    companion object {
        private const val DEFAULT_PYTHON_SECONDS = 45
        private const val DEFAULT_LINUX_SECONDS = 60
        private val PACKAGE_COMMAND = Regex("(?i)(^|[;&|()\\n]\\s*|\\bsudo\\s+)(apt|apt-get|aptitude|dpkg|snap|apk|rpm|dnf|yum|pacman|zypper|pip3?|python(?:3)?\\s+-m\\s+pip)\\b")
    }
}

private object PublicOnlyDns : Dns {
    override fun lookup(hostname: String): List<InetAddress> {
        val addresses = Dns.SYSTEM.lookup(hostname)
        require(addresses.isNotEmpty() && addresses.none(PublicNetworkPolicy::isBlockedAddress)) {
            "Local and private network addresses are blocked"
        }
        return addresses
    }
}

private fun ResponseBody.readLimited(limit: Long): String {
    val source = source()
    source.request(limit)
    val count = minOf(source.buffer.size, limit)
    return source.buffer.readUtf8(count)
}

private data class LimitedResponseText(val text: String, val truncated: Boolean)

private fun ResponseBody.readLimitedWithTruncation(limit: Long): LimitedResponseText {
    val source = source()
    val probe = limit.coerceAtLeast(1) + 1
    source.request(probe)
    val available = source.buffer.size
    val count = minOf(available, limit)
    return LimitedResponseText(source.buffer.readUtf8(count), available > limit)
}

@Serializable
private data class ConversationSearchToolItem(
    val nodeId: String,
    val conversationId: String,
    val conversationTitle: String,
    val snippet: String,
    val rank: Double,
)

@Serializable
internal data class HttpToolResponse(
    val url: String,
    val method: String,
    val status: Int,
    val contentType: String,
    val headers: Map<String, String>,
    val body: String,
    val truncated: Boolean = false,
)

@Serializable
private data class HttpWriteApprovalToolResult(
    val status: String,
    val approvalId: String,
    val confirmationText: String,
    val method: String,
    val url: String,
    val bodySha256: String,
    val expiresAt: Long,
    val instruction: String,
)

@Serializable
private data class MemorySaveToolResult(
    val saved: Boolean,
    val created: Boolean,
    val updated: Boolean,
    val id: String,
    val category: String,
    val content: String,
    val mergedMemoryId: String? = null,
)

@Serializable
private data class MemoryToolItem(
    val id: String,
    val content: String,
    val category: String,
    val enabled: Boolean,
    val updatedAt: Long,
)

@Serializable
private data class MemoryForgetToolResult(
    val forgotten: Boolean,
    val id: String,
)

@Serializable
private data class UbuntuToolResult(
    val stdout: String,
    val stderr: String,
    val exitCode: Int,
    val files: List<String>,
    val elapsedMs: Long,
    val timedOut: Boolean,
)

@Serializable
internal data class WebSearchResponse(
    val query: String,
    val engine: String = "DuckDuckGo",
    val results: List<WebSearchResult>,
)

@Serializable
internal data class WebSearchResult(val title: String, val url: String, val snippet: String)

@Serializable
internal data class WebFetchResponse(val url: String, val contentType: String, val text: String)
