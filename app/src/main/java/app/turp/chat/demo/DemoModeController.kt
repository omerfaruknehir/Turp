package app.turp.chat.demo

import androidx.room.withTransaction
import app.turp.chat.BuildConfig
import app.turp.chat.chat.ChatRepository
import app.turp.chat.data.ConversationEntity
import app.turp.chat.data.GenerationUsageEntity
import app.turp.chat.data.MessageEntity
import app.turp.chat.data.MessageRole
import app.turp.chat.data.MessageStatus
import app.turp.chat.data.ModelEntity
import app.turp.chat.data.ProjectEntity
import app.turp.chat.data.ProviderEntity
import app.turp.chat.data.ProviderKind
import app.turp.chat.data.SendMode
import app.turp.chat.data.TurpDatabase
import app.turp.chat.settings.AppPreferences
import app.turp.chat.settings.NewChatDefaults
import kotlinx.coroutines.delay
import kotlin.math.roundToLong

class DemoModeController(
    private val database: TurpDatabase,
    private val repository: ChatRepository,
    private val preferences: AppPreferences,
) {
    companion object {
        const val DEMO_PREFIX = "demo-"
        const val DEMO_PROVIDER_PREFIX = "demo-provider-"
        const val DEFAULT_PROVIDER_ID = "demo-provider-northstar"
        const val DEFAULT_MODEL_ID = "nova-12"
        const val WALKTHROUGH_CHAT_ID = "demo-chat-walkthrough"

        fun isDemoProviderId(providerId: String?): Boolean =
            providerId?.startsWith(DEMO_PROVIDER_PREFIX) == true

        fun isDemoConversationId(conversationId: String?): Boolean =
            conversationId?.startsWith("demo-chat-") == true
    }

    suspend fun reconcileAtStartup() {
        if (!BuildConfig.DEBUG) {
            preferences.updateDeveloperSettings { it.copy(demoModeEnabled = false) }
            return
        }
        if (preferences.developerSettings.value.demoModeEnabled) seed()
        else clearSeededDataAndRestoreSelections()
    }

    suspend fun setEnabled(enabled: Boolean) {
        if (!BuildConfig.DEBUG) {
            preferences.updateDeveloperSettings { it.copy(demoModeEnabled = false) }
            return
        }
        if (enabled) {
            seed()
            preferences.updateDeveloperSettings { it.copy(enabled = true, demoModeEnabled = true) }
        } else {
            preferences.updateDeveloperSettings { it.copy(demoModeEnabled = false) }
            clearSeededDataAndRestoreSelections()
        }
    }

    fun effectiveNewChatDefaults(base: NewChatDefaults): NewChatDefaults =
        if (BuildConfig.DEBUG && preferences.developerSettings.value.demoModeEnabled) {
            base.copy(
                selectedProviderId = DEFAULT_PROVIDER_ID,
                selectedModelId = DEFAULT_MODEL_ID,
                thinkingEnabled = true,
            )
        } else {
            base
        }

    suspend fun handlesConversation(conversationId: String): Boolean {
        if (!BuildConfig.DEBUG || !preferences.developerSettings.value.demoModeEnabled) return false
        val conversation = repository.conversationNow(conversationId) ?: return false
        return isDemoProviderId(conversation.selectedProviderId)
    }

    suspend fun submit(
        conversationId: String,
        text: String,
        attachmentIds: List<String>,
        mode: SendMode,
    ): String? {
        check(BuildConfig.DEBUG) { "Demo mode is debug-only" }
        check(handlesConversation(conversationId)) { "Conversation is not using a demo provider" }
        return if (mode == SendMode.QUEUE) {
            repository.submit(conversationId, text, attachmentIds, SendMode.QUEUE)
            null
        } else {
            repository.createExchange(conversationId, text, attachmentIds)
        }
    }

    suspend fun completeResponse(conversationId: String, firstAssistantId: String) {
        if (!BuildConfig.DEBUG) return
        var assistantId: String? = firstAssistantId
        while (assistantId != null && handlesConversation(conversationId)) {
            completeOne(assistantId)
            assistantId = repository.materializeNextPending(conversationId)
        }
    }

    private suspend fun completeOne(assistantId: String) {
        val assistant = repository.message(assistantId) ?: return
        if (assistant.status != MessageStatus.STREAMING) return
        val conversation = repository.conversationNow(assistant.conversationId) ?: return
        val model = repository.model(conversation.selectedProviderId, conversation.selectedModelId)
        val user = assistant.parentNodeId?.let { repository.message(it) }
        val prompt = user?.content.orEmpty()
        val response = cannedResponses.floorPick(prompt + "|" + conversation.selectedModelId)
        val reasoning = if (model?.supportsThinking == true) {
            reasoningSnippets.floorPick(prompt + "|" + assistantId)
        } else ""

        if (reasoning.isNotBlank()) {
            delay(180)
            if (repository.message(assistantId)?.status != MessageStatus.STREAMING) return
            repository.append(assistantId, "", reasoning)
        }

        for (chunk in response.toStreamingChunks()) {
            delay(120)
            if (repository.message(assistantId)?.status != MessageStatus.STREAMING) return
            repository.append(assistantId, chunk, "")
        }

        val inputTokens = (prompt.length / 4).coerceAtLeast(8).toLong()
        val outputTokens = (response.length / 4).coerceAtLeast(16).toLong()
        val inputPrice = model?.inputCacheMissUsdPerMillion ?: 0.0
        val outputPrice = model?.outputUsdPerMillion ?: 0.0
        val costMicros = (inputTokens * inputPrice + outputTokens * outputPrice).roundToLong()
        val costKnown = model?.pricingConfigured == true

        repository.finish(
            assistantId, MessageStatus.COMPLETE, null,
            inputTokens, outputTokens, 0, costMicros, costKnown,
        )
        repository.addUsage(
            assistant.conversationId, inputTokens, outputTokens, costMicros, costKnown,
        )
        repository.saveGenerationUsage(
            GenerationUsageEntity(
                id = "demo-usage-" + assistantId,
                assistantNodeId = assistantId,
                conversationId = assistant.conversationId,
                providerId = conversation.selectedProviderId,
                modelId = conversation.selectedModelId,
                roundIndex = 0,
                inputTokens = inputTokens,
                outputTokens = outputTokens,
                cachedInputTokens = 0,
                costMicros = costMicros,
                costKnown = costKnown,
                finishReason = "stop",
                status = "COMPLETE",
                createdAt = assistant.createdAt,
                updatedAt = System.currentTimeMillis(),
            ),
        )
    }

    private suspend fun seed() {
        database.withTransaction {
            clearSeededDataInTransaction()
            val now = System.currentTimeMillis()
            database.catalogDao().upsertProviders(demoProviders)
            database.catalogDao().upsertModels(demoModels)
            demoProjects(now).forEach { database.projectDao().insert(it) }
            demoChats(now).forEach { seedChat(it) }
        }
    }

    private suspend fun clearSeededDataAndRestoreSelections() {
        var fallbackProviderId: String? = null
        var fallbackModelId: String? = null
        database.withTransaction {
            val providers = database.catalogDao().allProviders()
                .filterNot { isDemoProviderId(it.id) }
            val models = database.catalogDao().allModels()
                .filterNot { isDemoProviderId(it.providerId) }
            val fallback = providers.asSequence()
                .mapNotNull { provider ->
                    models.firstOrNull { it.providerId == provider.id }?.let { provider.id to it.modelId }
                }
                .firstOrNull()
            fallbackProviderId = fallback?.first
            fallbackModelId = fallback?.second
            if (fallback != null) {
                database.conversationDao().replaceDemoModelSelections(
                    demoConversationPrefix = "demo-chat-",
                    demoProviderPrefix = DEMO_PROVIDER_PREFIX,
                    providerId = fallback.first,
                    modelId = fallback.second,
                )
            }
            clearSeededDataInTransaction()
        }

        val defaults = preferences.newChatDefaults.value
        if (isDemoProviderId(defaults.selectedProviderId) &&
            fallbackProviderId != null && fallbackModelId != null
        ) {
            preferences.setNewChatDefaults(
                defaults.copy(
                    selectedProviderId = fallbackProviderId!!,
                    selectedModelId = fallbackModelId!!,
                ),
            )
        }
    }

    private suspend fun clearSeededDataInTransaction() {
        database.conversationDao().deleteByIdPrefix(DEMO_PREFIX)
        database.projectDao().deleteByIdPrefix(DEMO_PREFIX)
        database.catalogDao().deleteModelsByProviderPrefix(DEMO_PROVIDER_PREFIX)
        database.catalogDao().deleteProvidersByIdPrefix(DEMO_PROVIDER_PREFIX)
    }

    private suspend fun seedChat(spec: DemoChatSpec) {
        val messages = mutableListOf<MessageEntity>()
        var parent: String? = null
        val branch = "demo-branch-" + spec.slug
        spec.exchanges.forEachIndexed { index, exchange ->
            val userId = "demo-msg-" + spec.slug + "-" + index + "-u"
            val assistantId = "demo-msg-" + spec.slug + "-" + index + "-a"
            val created = spec.createdAt + index * 120_000L
            messages += MessageEntity(
                nodeId = userId,
                conversationId = spec.id,
                parentNodeId = parent,
                branchId = branch,
                role = MessageRole.USER,
                content = exchange.first,
                status = MessageStatus.COMPLETE,
                createdAt = created,
                updatedAt = created,
            )
            messages += MessageEntity(
                nodeId = assistantId,
                conversationId = spec.id,
                parentNodeId = userId,
                branchId = branch,
                role = MessageRole.ASSISTANT,
                content = exchange.second,
                status = MessageStatus.COMPLETE,
                providerId = spec.providerId,
                modelId = spec.modelId,
                inputTokens = (exchange.first.length / 4).coerceAtLeast(8).toLong(),
                outputTokens = (exchange.second.length / 4).coerceAtLeast(16).toLong(),
                costMicros = 240 + index * 75L,
                costKnown = true,
                createdAt = created + 1,
                updatedAt = created + 1,
            )
            parent = assistantId
        }

        database.conversationDao().upsert(
            ConversationEntity(
                id = spec.id,
                title = spec.title,
                createdAt = spec.createdAt,
                updatedAt = spec.createdAt + spec.exchanges.size * 120_000L,
                activeLeafNodeId = parent,
                selectedProviderId = spec.providerId,
                selectedModelId = spec.modelId,
                totalInputTokens = messages.sumOf { it.inputTokens },
                totalOutputTokens = messages.sumOf { it.outputTokens },
                totalCostMicros = messages.sumOf { it.costMicros },
                lastReadAt = if (spec.unread) 0 else Long.MAX_VALUE,
                autoTitle = false,
                thinkingEnabled = spec.modelId.hashCode() % 2 == 0,
                webSearchEnabled = spec.web,
                agentPythonEnabled = spec.python,
                archived = spec.archived,
                pinned = spec.pinned,
                projectId = spec.projectId,
                archivedAt = if (spec.archived) spec.createdAt + 1_000 else null,
            ),
        )
        messages.forEach { database.messageDao().insert(it) }
    }

    private data class DemoChatSpec(
        val id: String,
        val slug: String,
        val title: String,
        val providerId: String,
        val modelId: String,
        val projectId: String?,
        val createdAt: Long,
        val exchanges: List<Pair<String, String>>,
        val pinned: Boolean = false,
        val archived: Boolean = false,
        val unread: Boolean = false,
        val web: Boolean = false,
        val python: Boolean = false,
    )

    private data class DemoProviderSpec(
        val id: String,
        val name: String,
        val kind: ProviderKind,
        val modelPrefix: String,
    )

    private val providerSpecs = listOf(
        DemoProviderSpec("demo-provider-northstar", "Demo · Northstar AI", ProviderKind.OPENAI_COMPATIBLE, "Nova"),
        DemoProviderSpec("demo-provider-aurora", "Demo · Aurora Labs", ProviderKind.ANTHROPIC, "Aster"),
        DemoProviderSpec("demo-provider-prism", "Demo · Prism Cloud", ProviderKind.GEMINI, "Prism"),
        DemoProviderSpec("demo-provider-orbit", "Demo · Orbit Inference", ProviderKind.OPENAI_COMPATIBLE, "Orbit"),
        DemoProviderSpec("demo-provider-local", "Demo · Local Runtime", ProviderKind.OPENAI_COMPATIBLE, "Local"),
    )

    private val demoProviders = providerSpecs.map {
        ProviderEntity(
            id = it.id,
            displayName = it.name,
            kind = it.kind,
            baseUrl = "https://demo.invalid/" + it.id,
            enabled = true,
            registered = true,
            apiKeyRequired = false,
        )
    }

    private val demoModels = providerSpecs.flatMapIndexed { providerIndex, provider ->
        (1..20).map { number ->
            val supportsThinking = number % 2 == 0 || number % 5 == 0
            val isFree = number % 10 == 0
            val context = listOf(32_000, 64_000, 128_000, 200_000, 1_000_000)[(number + providerIndex) % 5]
            ModelEntity(
                providerId = provider.id,
                modelId = provider.modelPrefix.lowercase() + "-" + number.toString().padStart(2, '0'),
                displayName = provider.modelPrefix + " " + number + "." + (providerIndex + 1),
                contextWindow = context,
                maxOutputTokens = listOf(4_096, 8_192, 16_384, 32_768)[number % 4],
                inputCacheHitUsdPerMillion = if (isFree) 0.0 else 0.05 + providerIndex * 0.03,
                inputCacheMissUsdPerMillion = if (isFree) 0.0 else 0.40 + number * 0.06 + providerIndex * 0.15,
                outputUsdPerMillion = if (isFree) 0.0 else 1.10 + number * 0.11 + providerIndex * 0.25,
                pricingConfigured = true,
                supportsVision = number % 3 != 0,
                supportsFiles = number % 4 != 0,
                supportsThinking = supportsThinking,
                supportsTools = number % 5 != 1,
                description = "Debug demo model " + number + " from " + provider.name + ". No network requests are made.",
                reasoningMetadataAvailable = supportsThinking,
                reasoningEffortsCsv = if (supportsThinking) "LOW,MEDIUM,HIGH" else "",
                reasoningDefaultEffort = if (supportsThinking) "MEDIUM" else "",
                reasoningDefaultEnabled = supportsThinking,
                metadataSource = "Turp demo mode",
                metadataUpdatedAt = 1,
            )
        }
    }

    private fun demoProjects(now: Long) = listOf(
        ProjectEntity("demo-project-work", "[Demo] Workbench", 0xFF9F244AL, now - 7_000_000, now),
        ProjectEntity("demo-project-research", "[Demo] Research", 0xFF5D6070L, now - 6_000_000, now),
        ProjectEntity("demo-project-personal", "[Demo] Personal", 0xFF5D625FL, now - 5_000_000, now),
    )

    private fun demoChats(now: Long): List<DemoChatSpec> {
        val p = providerSpecs
        fun model(provider: Int, number: Int) =
            p[provider].modelPrefix.lowercase() + "-" + number.toString().padStart(2, '0')
        return listOf(
            DemoChatSpec(
                WALKTHROUGH_CHAT_ID, "walkthrough", "Turp demo walkthrough",
                p[0].id, model(0, 12), "demo-project-work", now - 3_600_000,
                listOf(
                    "What is this chat?" to
                        "This is Turp's **debug demo mode**. Every provider, model, message, token count, and price here is local stub data. Try the model selector: it contains **100 models across 5 demo providers**.",
                    "Show me something that stresses Markdown rendering." to
                        """Here is a compact rendering sample:

| Surface | Demo data |
| --- | --- |
| Providers | 5 |
| Models | 100 |
| Network calls | 0 |

```kotlin
val safe = BuildConfig.DEBUG && demoModeEnabled
println("Turp demo active")
```

- Lists render normally
- **Bold**, *italic*, and `inline code` are included
- New demo replies stream in small chunks""".trimIndent(),
                ),
                pinned = true, unread = true, web = true, python = true,
            ),
            DemoChatSpec(
                "demo-chat-kotlin", "kotlin", "Debugging a Kotlin coroutine",
                p[1].id, model(1, 7), "demo-project-work", now - 7_200_000,
                listOf(
                    "Why can a coroutine appear stuck even when the thread is alive?" to
                        "A coroutine can be suspended while the underlying thread remains healthy. Inspect the suspension point, dispatcher, mutex ownership, channel backpressure, and cancellation state before blaming the thread.",
                    "Give me a short checklist." to
                        "1. Capture coroutine dumps.\n2. Check mutex/channel ownership.\n3. Verify dispatcher saturation.\n4. Confirm cancellation propagation.\n5. Reproduce with structured timing logs.",
                ),
                python = true,
            ),
            DemoChatSpec(
                "demo-chat-ci", "ci", "Fix flaky Android CI",
                p[3].id, model(3, 14), "demo-project-work", now - 10_800_000,
                listOf(
                    "Our emulator test fails around one run in twenty." to
                        "Treat emulator readiness and test determinism separately. Record boot completion, animation state, package install status, device connectivity, screenshots, and the exact APK commit.",
                    "What should I log?" to
                        "At minimum: adb state, boot-completed property, instrumentation output, logcat crash buffer, screenshots, UI hierarchy, and build SHA.",
                ),
                unread = true,
            ),
            DemoChatSpec(
                "demo-chat-api", "api", "Review an API design",
                p[0].id, model(0, 5), "demo-project-work", now - 14_400_000,
                listOf(
                    "Should retries be hidden inside the SDK?" to
                        "Only for operations that are safe to retry. Expose retry metadata and preserve idempotency boundaries; silent retries around non-idempotent writes can duplicate side effects.",
                    "What about rate limits?" to
                        "Return structured retry timing when available, use jittered backoff, and let callers set a maximum retry budget.",
                ),
            ),
            DemoChatSpec(
                "demo-chat-research", "research", "Summarize research notes",
                p[2].id, model(2, 18), "demo-project-research", now - 18_000_000,
                listOf(
                    "Turn these notes into a research summary." to
                        "The notes point to three themes: latency matters more than peak throughput for interactive work, local caching changes perceived reliability, and instrumentation must separate model time from transport and UI time.",
                    "What should I verify next?" to
                        "Verify claims against primary sources, label measurements by hardware/software version, and separate controlled benchmarks from anecdotes.",
                ),
                web = true,
            ),
            DemoChatSpec(
                "demo-chat-quantum", "quantum", "Explain quantum tunneling",
                p[2].id, model(2, 9), "demo-project-research", now - 21_600_000,
                listOf(
                    "Explain tunneling without saying a particle drills through a wall." to
                        "Quantum tunneling comes from the wavefunction having non-zero amplitude beyond a classically forbidden region. A measurement can therefore find the particle on the other side even when a classical trajectory would not cross the barrier.",
                    "Give me the intuition in one sentence." to
                        "The quantum state does not stop abruptly at the barrier, so there is a finite probability of detecting the particle beyond it.",
                ),
            ),
            DemoChatSpec(
                "demo-chat-sql", "sql", "Optimize a slow SQL query",
                p[4].id, model(4, 16), "demo-project-research", now - 25_200_000,
                listOf(
                    "A query filters by account and sorts by created_at. What index first?" to
                        "Test a composite index beginning with the equality-filtered account column and then the ordering column, such as `(account_id, created_at DESC)`, while checking the real query plan.",
                    "Any caveat?" to
                        "Selectivity, covering columns, write cost, and database-specific planner behavior can change the best index.",
                ),
            ),
            DemoChatSpec(
                "demo-chat-trip", "trip", "Plan a relaxed weekend",
                p[1].id, model(1, 11), "demo-project-personal", now - 28_800_000,
                listOf(
                    "Make a relaxed two-day city plan." to
                        "Keep each day to one compact area: slow breakfast, one major sight, flexible lunch, a nearby neighborhood walk, then dinner close to where you finish. Reserve only the time-sensitive attraction.",
                    "How do I avoid overplanning?" to
                        "Choose one anchor activity per half-day and keep the rest optional. Travel time and queues consume more of a weekend than most itineraries admit.",
                ),
            ),
            DemoChatSpec(
                "demo-chat-meals", "meals", "Simple meal prep",
                p[4].id, model(4, 10), "demo-project-personal", now - 32_400_000,
                listOf(
                    "I want three lunches using mostly the same ingredients." to
                        "Use one grain, one roasted vegetable tray, and one protein base, then change the sauce and garnish: lemon-herb, tomato-pepper, and yogurt-garlic.",
                    "Can one be vegetarian?" to
                        "Yes. Replace the protein with chickpeas or lentils in one portion and keep the same vegetables and grain.",
                ),
            ),
            DemoChatSpec(
                "demo-chat-dashboard", "dashboard", "Design a monitoring dashboard",
                p[3].id, model(3, 6), null, now - 36_000_000,
                listOf(
                    "What belongs above the fold?" to
                        "Show health, current impact, and change: availability, latency/error rate, active incidents, and a compact comparison to the previous period.",
                    "Should every metric be a chart?" to
                        "No. A number with a trend can be clearer than a chart when history is not needed for the decision.",
                ),
                unread = true,
            ),
            DemoChatSpec(
                "demo-chat-launch", "launch", "Product launch checklist",
                p[0].id, model(0, 20), null, now - 40_000_000,
                listOf(
                    "Give me a launch-day checklist." to
                        "Confirm rollback, monitoring, ownership, support coverage, data migrations, rate limits, release notes, and a single source of truth for status.",
                    "What is usually forgotten?" to
                        "Rollback validation and support handoff. Teams often prove deployment works but do not rehearse recovery.",
                ),
            ),
            DemoChatSpec(
                "demo-chat-archived", "archived", "Old migration notes",
                p[4].id, model(4, 3), "demo-project-work", now - 86_400_000,
                listOf(
                    "Keep this old migration note for reference." to
                        "Archived demo conversation retained. It exists to exercise Turp's archived-chat list and restore flow.",
                ),
                archived = true,
            ),
        )
    }

    private val reasoningSnippets = listOf(
        "I’m separating the user’s goal from implementation details, then checking the smallest useful answer.",
        "I’m comparing the likely failure modes and prioritizing the one that best matches the observed behavior.",
        "I’m keeping this response deterministic because demo mode must remain offline and reproducible.",
        "I’m choosing an example that exercises Turp’s rendering without implying a live external lookup.",
    )

    private val cannedResponses = listOf(
        """The main thing I would change is the **boundary between state and presentation**.

1. Make the state transition explicit.
2. Keep side effects behind one function.
3. Add one regression test for the failure you actually saw.

That usually fixes the bug without making the screen harder to reason about.""".trimIndent(),
        """Here is a compact comparison:

| Option | Strength | Trade-off |
| --- | --- | --- |
| A | Simple | Less flexible |
| B | Flexible | More state |
| C | Fast to ship | More cleanup later |

For a production path, start with **A** unless you already know you need B’s extra control.""".trimIndent(),
        """A useful debugging sequence is:

```text
reproduce
→ isolate the state transition
→ log ownership/timing
→ remove unrelated variables
→ add a regression test
→ patch the smallest layer
```

Prove which layer owns the bad behavior before changing code.""".trimIndent(),
        "Treat that as two separate questions: what the system **does now**, and what contract you want it to guarantee. Once those are written separately, the implementation choice is usually clearer.",
        "Make the default path boring. Put advanced behavior behind an explicit capability or setting, keep failure states visible, and avoid silently falling back to behavior the user did not request.",
        "Measure the current behavior first, change one variable, then measure again. If several changes land together, you lose the ability to explain why the result improved or regressed.",
        "For UI work, optimize for hierarchy before decoration: primary action, current state, secondary controls, then metadata. If everything has equal visual weight, the interface feels noisy.",
        "This is a good candidate for a deterministic test fixture. Seed enough data to hit scrolling, long labels, empty states, selected states, and mixed capabilities, then keep the fixture stable.",
        "If latency matters, split it into network, model, persistence, and rendering time. A single end-to-end number tells you something is slow; it does not tell you what to fix.",
        "Preserve the partial result, show a concise explanation, and provide a retry path. Silent recovery is useful only when it cannot surprise the user or duplicate work.",
        "A simple architecture is often: immutable input → validated domain state → one side-effect boundary → observable result. That keeps tests small and cancellation behavior easier to verify.",
        "Before adding another setting, check whether the choice can be inferred safely from context. Settings are valuable when users genuinely have different preferences.",
        "Use three rollout stages: internal fixture, debug build validation, then production telemetry limited to failures and performance. Each stage should have a rollback condition.",
        "The code can be shorter without becoming clever. Extract the repeated policy decision, leave call sites explicit, and avoid abstractions that only save one or two lines.",
        "For a large list, test awkward cases deliberately: 100+ rows, long provider names, mixed badges, rapid filtering, selection near the bottom, and switching filters mid-scroll.",
        "Preserve the user’s existing data and namespace generated fixtures. Demo data should be removable by identity, not by broad destructive cleanup.",
        "The answer depends on the failure budget. If correctness matters more than speed, validate before committing state. If responsiveness dominates, stage optimistic UI with rollback.",
        "Make the state machine visible in logs: idle → queued → streaming → complete/interrupted/error. Bugs become easier to reproduce when transitions are explicit.",
        "Add a guard for stale state. Any async completion should verify that the object it is updating is still active before writing the result.",
        "For a demo, realism matters more than randomness. Use varied but deterministic fixtures so every screen looks populated while screenshots and regression tests stay repeatable.",
    )

    private fun List<String>.floorPick(seed: String): String =
        this[Math.floorMod(seed.hashCode(), size)]

    private fun String.toStreamingChunks(): List<String> {
        if (length < 90) return listOf(this)
        val result = mutableListOf<String>()
        var start = 0
        while (start < length) {
            var end = (start + 72).coerceAtMost(length)
            if (end < length) {
                val space = lastIndexOf(' ', end)
                if (space > start + 36) end = space + 1
            }
            result += substring(start, end)
            start = end
        }
        return result
    }
}
