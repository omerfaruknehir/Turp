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
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request

data class OpenRouterFreeModelAllowance(
    val limit: Int?,
    val remaining: Int?,
    val used: Int?,
)

data class OpenRouterKeySnapshot(
    val label: String?,
    val limitUsd: Double?,
    val limitRemainingUsd: Double?,
    val limitReset: String?,
    val usageUsd: Double?,
    val usageDailyUsd: Double?,
    val usageWeeklyUsd: Double?,
    val usageMonthlyUsd: Double?,
    val byokUsageUsd: Double?,
    val byokUsageDailyUsd: Double?,
    val byokUsageWeeklyUsd: Double?,
    val byokUsageMonthlyUsd: Double?,
    val includeByokInLimit: Boolean?,
    val isFreeTier: Boolean?,
    val allowedDataRegions: List<String>,
    val expiresAtEpochSeconds: Long?,
    val freeModelDailyRequests: OpenRouterFreeModelAllowance?,
    val fetchedAtEpochMs: Long,
)

sealed interface OpenRouterKeyState {
    data object Unavailable : OpenRouterKeyState
    data class Loading(val previous: OpenRouterKeySnapshot? = null) : OpenRouterKeyState
    data class Loaded(val snapshot: OpenRouterKeySnapshot) : OpenRouterKeyState
    data class Error(val message: String, val previous: OpenRouterKeySnapshot? = null) : OpenRouterKeyState
}

class OpenRouterManager(
    private val secureStore: SecureStore,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .build(),
) {
    private data class Cache(val snapshot: OpenRouterKeySnapshot, val validUntil: Long)

    private val mutex = Mutex()
    private val caches = ConcurrentHashMap<String, Cache>()
    private val _keyStates = MutableStateFlow<Map<String, OpenRouterKeyState>>(emptyMap())
    val keyStates: StateFlow<Map<String, OpenRouterKeyState>> = _keyStates.asStateFlow()

    suspend fun keyInfo(provider: ProviderEntity, forceRefresh: Boolean = false): OpenRouterKeySnapshot =
        mutex.withLock {
            require(ModelRequestPolicy.isOpenRouter(provider)) { "Provider profile is not OpenRouter" }
            val providerId = provider.id
            val apiKey = secureStore.apiKey(providerId)
            if (apiKey.isBlank()) {
                update(providerId, OpenRouterKeyState.Unavailable)
                throw IllegalStateException("Add an OpenRouter API key first")
            }

            val now = System.currentTimeMillis()
            val cached = caches[providerId]
            if (!forceRefresh && cached != null && now < cached.validUntil) {
                update(providerId, OpenRouterKeyState.Loaded(cached.snapshot))
                return cached.snapshot
            }

            val previous = cached?.snapshot
            update(providerId, OpenRouterKeyState.Loading(previous))
            try {
                val snapshot = withContext(Dispatchers.IO) { fetch(provider, apiKey) }
                caches[providerId] = Cache(snapshot, System.currentTimeMillis() + CACHE_MS)
                update(providerId, OpenRouterKeyState.Loaded(snapshot))
                snapshot
            } catch (error: Throwable) {
                val message = error.message?.take(500) ?: "OpenRouter key usage could not be loaded"
                update(providerId, OpenRouterKeyState.Error(message, previous))
                throw error
            }
        }

    fun clear(providerId: String) {
        caches.remove(providerId)
        _keyStates.update { it - providerId }
    }

    private suspend fun fetch(provider: ProviderEntity, apiKey: String): OpenRouterKeySnapshot {
        val request = Request.Builder()
            .url(ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.ACCOUNT))
            .header("Authorization", "Bearer $apiKey")
            .header("Accept", "application/json")
            .header("User-Agent", "Turp-Android")
            .get()
            .build()
        return client.newCall(request).useCancellable { response ->
            if (!response.isSuccessful) {
                val detail = response.body?.readErrorSnippet().orEmpty().take(800)
                throw ProviderHttpException(
                    response.code,
                    "OpenRouter key lookup failed (${response.code}): " +
                        detail.ifBlank { response.message },
                )
            }
            val raw = response.body?.string()
                ?: throw ProviderProtocolException("OpenRouter returned an empty key response")
            val root = runCatching { ProviderJson.parseToJsonElement(raw).jsonObject }
                .getOrElse { throw ProviderProtocolException("OpenRouter returned invalid key JSON", it) }
            OpenRouterKeyParser.parse(root)
        }
    }

    private fun update(providerId: String, state: OpenRouterKeyState) {
        _keyStates.update { it + (providerId to state) }
    }

    private companion object {
        const val CACHE_MS = 60_000L
    }
}

internal object OpenRouterKeyParser {
    fun parse(root: JsonObject, nowMs: Long = System.currentTimeMillis()): OpenRouterKeySnapshot {
        val data = root["data"] as? JsonObject
            ?: throw ProviderProtocolException("OpenRouter key response did not contain data")
        val free = data["free_model_daily_requests"] as? JsonObject
        val regions = (data["allowed_data_regions"] as? JsonArray)
            ?.mapNotNull { it.jsonPrimitive.contentOrNull }
            .orEmpty()
        return OpenRouterKeySnapshot(
            label = data["label"]?.jsonPrimitive?.contentOrNull,
            limitUsd = data["limit"]?.jsonPrimitive?.doubleOrNull,
            limitRemainingUsd = data["limit_remaining"]?.jsonPrimitive?.doubleOrNull,
            limitReset = data["limit_reset"]?.jsonPrimitive?.contentOrNull,
            usageUsd = data["usage"]?.jsonPrimitive?.doubleOrNull,
            usageDailyUsd = data["usage_daily"]?.jsonPrimitive?.doubleOrNull,
            usageWeeklyUsd = data["usage_weekly"]?.jsonPrimitive?.doubleOrNull,
            usageMonthlyUsd = data["usage_monthly"]?.jsonPrimitive?.doubleOrNull,
            byokUsageUsd = data["byok_usage"]?.jsonPrimitive?.doubleOrNull,
            byokUsageDailyUsd = data["byok_usage_daily"]?.jsonPrimitive?.doubleOrNull,
            byokUsageWeeklyUsd = data["byok_usage_weekly"]?.jsonPrimitive?.doubleOrNull,
            byokUsageMonthlyUsd = data["byok_usage_monthly"]?.jsonPrimitive?.doubleOrNull,
            includeByokInLimit = data["include_byok_in_limit"]?.jsonPrimitive?.booleanOrNull,
            isFreeTier = data["is_free_tier"]?.jsonPrimitive?.booleanOrNull,
            allowedDataRegions = regions,
            expiresAtEpochSeconds = data["expires_at"]?.jsonPrimitive?.contentOrNull
                ?.let { raw -> runCatching { Instant.parse(raw).epochSecond }.getOrNull() },
            freeModelDailyRequests = free?.let {
                OpenRouterFreeModelAllowance(
                    limit = it["limit"]?.jsonPrimitive?.intOrNull,
                    remaining = it["remaining"]?.jsonPrimitive?.intOrNull,
                    used = it["used"]?.jsonPrimitive?.intOrNull,
                )
            },
            fetchedAtEpochMs = nowMs,
        )
    }
}
