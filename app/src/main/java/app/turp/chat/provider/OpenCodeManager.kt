package app.turp.chat.provider

import app.turp.chat.data.ProviderEntity
import app.turp.chat.security.SecureStore
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request

data class OpenCodeUsageWindow(
    val status: String,
    val usedPercent: Double,
    val resetsAtEpochSeconds: Long?,
)

data class OpenCodeUsageSnapshot(
    val rolling: OpenCodeUsageWindow?,
    val weekly: OpenCodeUsageWindow?,
    val monthly: OpenCodeUsageWindow?,
    val fetchedAtEpochMs: Long,
)

sealed interface OpenCodeUsageState {
    data object Unavailable : OpenCodeUsageState
    data class Loading(val previous: OpenCodeUsageSnapshot? = null) : OpenCodeUsageState
    data class Loaded(val snapshot: OpenCodeUsageSnapshot) : OpenCodeUsageState
    data class Error(val message: String, val previous: OpenCodeUsageSnapshot? = null) : OpenCodeUsageState
}

class OpenCodeManager(
    private val secureStore: SecureStore,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .build(),
) {
    private data class Cache(val snapshot: OpenCodeUsageSnapshot, val validUntil: Long)

    private val mutex = Mutex()
    private val caches = ConcurrentHashMap<String, Cache>()
    private val _usageStates = MutableStateFlow<Map<String, OpenCodeUsageState>>(emptyMap())
    val usageStates: StateFlow<Map<String, OpenCodeUsageState>> = _usageStates.asStateFlow()

    suspend fun usage(provider: ProviderEntity, forceRefresh: Boolean = false): OpenCodeUsageSnapshot =
        mutex.withLock {
            require(ModelRequestPolicy.isOpenCodeGo(provider)) { "Provider profile is not OpenCode Go" }
            val providerId = provider.id
            val apiKey = secureStore.apiKey(providerId)
            if (apiKey.isBlank()) {
                update(providerId, OpenCodeUsageState.Unavailable)
                throw IllegalStateException("Add an OpenCode Go API key first")
            }

            val now = System.currentTimeMillis()
            val cached = caches[providerId]
            if (!forceRefresh && cached != null && now < cached.validUntil) {
                update(providerId, OpenCodeUsageState.Loaded(cached.snapshot))
                return cached.snapshot
            }

            val previous = cached?.snapshot
            update(providerId, OpenCodeUsageState.Loading(previous))
            try {
                val snapshot = withContext(Dispatchers.IO) { fetch(provider, apiKey) }
                caches[providerId] = Cache(snapshot, System.currentTimeMillis() + CACHE_MS)
                update(providerId, OpenCodeUsageState.Loaded(snapshot))
                snapshot
            } catch (error: Throwable) {
                val message = error.message?.take(500) ?: "OpenCode Go usage could not be loaded"
                update(providerId, OpenCodeUsageState.Error(message, previous))
                throw error
            }
        }

    fun clear(providerId: String) {
        caches.remove(providerId)
        _usageStates.update { it - providerId }
    }

    private suspend fun fetch(provider: ProviderEntity, apiKey: String): OpenCodeUsageSnapshot {
        val request = Request.Builder()
            .url(ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.ACCOUNT))
            .header("Authorization", "Bearer " + apiKey)
            .header("Accept", "application/json")
            .header("User-Agent", "Turp-Android")
            .get()
            .build()
        return client.newCall(request).useCancellable { response ->
            if (!response.isSuccessful) {
                val detail = response.body?.readErrorSnippet().orEmpty().take(800)
                throw ProviderHttpException(
                    response.code,
                    "OpenCode Go usage failed (" + response.code + "): " +
                        detail.ifBlank { response.message },
                )
            }
            val raw = response.body?.string()
                ?: throw ProviderProtocolException("OpenCode Go returned an empty usage response")
            val root = runCatching { ProviderJson.parseToJsonElement(raw).jsonObject }
                .getOrElse { throw ProviderProtocolException("OpenCode Go returned invalid usage JSON", it) }
            OpenCodeUsageParser.parse(root)
        }
    }

    private fun update(providerId: String, state: OpenCodeUsageState) {
        _usageStates.update { it + (providerId to state) }
    }

    private companion object {
        const val CACHE_MS = 60_000L
    }
}


internal object OpenCodeUsageParser {
    fun parse(root: JsonObject, nowMs: Long = System.currentTimeMillis()): OpenCodeUsageSnapshot {
        val usage = root["usage"] as? JsonObject
            ?: throw ProviderProtocolException("OpenCode Go usage response did not contain usage windows")
        return OpenCodeUsageSnapshot(
            rolling = parseWindow(usage["rolling"] as? JsonObject),
            weekly = parseWindow(usage["weekly"] as? JsonObject),
            monthly = parseWindow(usage["monthly"] as? JsonObject),
            fetchedAtEpochMs = nowMs,
        )
    }

    private fun parseWindow(value: JsonObject?): OpenCodeUsageWindow? {
        value ?: return null
        val percent = value["percent"]?.jsonPrimitive?.doubleOrNull ?: return null
        val status = value["status"]?.jsonPrimitive?.contentOrNull.orEmpty().ifBlank { "ok" }
        val resetsAt = value["resetsAt"]?.jsonPrimitive?.contentOrNull
            ?.let { raw -> runCatching { Instant.parse(raw).epochSecond }.getOrNull() }
        return OpenCodeUsageWindow(
            status = status,
            usedPercent = percent.coerceIn(0.0, 100.0),
            resetsAtEpochSeconds = resetsAt,
        )
    }
}
