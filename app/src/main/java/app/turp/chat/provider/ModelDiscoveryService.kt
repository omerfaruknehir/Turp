package app.turp.chat.provider

import app.turp.chat.data.ProviderEntity
import app.turp.chat.data.ProviderKind
import app.turp.chat.data.ProviderProfile
import app.turp.chat.data.ProviderProtocol
import app.turp.chat.data.ThinkingEffort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

data class DiscoveredModel(
    val id: String,
    val displayName: String,
    val contextWindow: Int? = null,
    val maxOutputTokens: Int? = null,
    val supportsThinking: Boolean? = null,
    val supportsVision: Boolean? = null,
    val supportsFiles: Boolean? = null,
    val supportsTools: Boolean? = null,
    val supportsImageGeneration: Boolean? = null,
    val description: String = "",
    val createdAtEpochSeconds: Long = 0,
    val inputCacheHitUsdPerMillion: Double? = null,
    val inputCacheMissUsdPerMillion: Double? = null,
    val outputUsdPerMillion: Double? = null,
    val reasoningMetadataAvailable: Boolean = false,
    val reasoningEfforts: List<ThinkingEffort> = emptyList(),
    val reasoningDefaultEffort: ThinkingEffort? = null,
    val reasoningDefaultEnabled: Boolean = false,
    val reasoningMandatory: Boolean = false,
    val reasoningSupportsMaxTokens: Boolean = false,
    val metadataSource: String = "",
)

class ModelDiscoveryService(
    private val oauth: OpenAiOAuthManager?,
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .callTimeout(45, TimeUnit.SECONDS)
        .build(),
) {
    suspend fun discover(provider: ProviderEntity, apiKey: String): List<DiscoveredModel> =
        discover(
            kind = provider.effectiveProtocol.legacyKind(),
            rawBaseUrl = provider.baseUrl,
            apiKey = apiKey,
            customHeadersJson = provider.customHeadersJson,
            providerId = provider.id,
            configuredProvider = provider,
        )

    suspend fun discover(
        kind: ProviderKind,
        rawBaseUrl: String,
        apiKey: String,
        customHeadersJson: String,
        providerId: String? = null,
        configuredProvider: ProviderEntity? = null,
    ): List<DiscoveredModel> = withContext(Dispatchers.IO) {
        if (configuredProvider?.effectiveProtocol == ProviderProtocol.OPENCODE_V2) {
            return@withContext discoverOpenCodeV2(configuredProvider, apiKey)
        }
        if (kind == ProviderKind.OPENAI_OAUTH) {
            val oauthManager = requireNotNull(oauth) { "OAuth model discovery requires an OAuth manager" }
            return@withContext oauthManager.modelCatalog(providerId ?: OpenAiOAuthManager.PROVIDER_ID, forceRefresh = true).map { model ->
                DiscoveredModel(
                    id = model.id,
                    displayName = model.displayName,
                    contextWindow = model.contextWindow,
                    maxOutputTokens = model.maxOutputTokens,
                    supportsThinking = model.supportsThinking,
                    supportsVision = true,
                    supportsFiles = false,
                    supportsTools = true,
                    supportsImageGeneration = model.supportsImageGeneration,
                )
            }
        }
        val baseUrl = ProviderEndpointPolicy.validate(rawBaseUrl)
        val customHeaders = parseHeaders(customHeadersJson)
        val openRouter = configuredProvider?.let(ModelRequestPolicy::isOpenRouter)
            ?: ModelRequestPolicy.isOpenRouterBaseUrl(baseUrl)
        val officialOpenAi = configuredProvider?.let(ModelRequestPolicy::isOfficialOpenAi)
            ?: ModelRequestPolicy.isOfficialOpenAiBaseUrl(baseUrl)
        val openCodeGo = configuredProvider?.let(ModelRequestPolicy::isOpenCodeGo)
            ?: ModelRequestPolicy.isOpenCodeGoBaseUrl(baseUrl)
        val openCodeZen = configuredProvider?.let(ModelRequestPolicy::isOpenCodeZen)
            ?: ModelRequestPolicy.isOpenCodeZenBaseUrl(baseUrl)
        val qwenCloud = configuredProvider?.let(ModelRequestPolicy::isAlibabaModelStudio)
            ?: (ModelRequestPolicy.matchesPresetId(providerId, "qwen-cloud") || ModelRequestPolicy.isQwenCloudBaseUrl(baseUrl))
        val modelListUrl = configuredProvider?.let {
            ProviderEndpointResolver.resolve(it, ProviderEndpointKey.MODELS)
        } ?: "$baseUrl/models"
        val collected = mutableListOf<DiscoveredModel>()
        val seenCursors = mutableSetOf<String>()
        var cursor: String? = null
        for (page in 0 until MAX_PAGES) {
            val endpoint = modelListUrl.toHttpUrl().newBuilder().apply {
                when (kind) {
                    ProviderKind.OPENAI_COMPATIBLE -> if (openRouter) {
                        addQueryParameter("output_modalities", "all")
                        addQueryParameter("limit", MAX_MODELS.toString())
                    }
                    ProviderKind.OPENAI_OAUTH -> error("OAuth discovery is handled before paging")
                    ProviderKind.ANTHROPIC -> {
                        addQueryParameter("limit", "100")
                        cursor?.let { addQueryParameter("after_id", it) }
                    }
                    ProviderKind.GEMINI -> {
                        addQueryParameter("pageSize", MAX_MODELS.toString())
                        cursor?.let { addQueryParameter("pageToken", it) }
                    }
                }
            }.build()
            val body = fetchPage(kind, endpoint, apiKey, customHeaders, largeCatalog = openRouter)
            collected += when (kind) {
                ProviderKind.OPENAI_COMPATIBLE, ProviderKind.ANTHROPIC -> parseDataModels(
                    body["data"] as? JsonArray,
                    baseUrl,
                    openRouterOverride = openRouter,
                    officialOpenAiOverride = officialOpenAi,
                )
                ProviderKind.OPENAI_OAUTH -> error("OAuth discovery is handled before paging")
                ProviderKind.GEMINI -> parseGeminiModels(body["models"] as? JsonArray)
            }
            if (collected.size >= MAX_MODELS || kind == ProviderKind.OPENAI_COMPATIBLE) break
            val next = when (kind) {
                ProviderKind.OPENAI_COMPATIBLE -> null
                ProviderKind.OPENAI_OAUTH -> null
                ProviderKind.ANTHROPIC -> if (body["has_more"]?.jsonPrimitive?.booleanOrNull == true) {
                    body["last_id"]?.jsonPrimitive?.contentOrNull
                } else null
                ProviderKind.GEMINI -> body["nextPageToken"]?.jsonPrimitive?.contentOrNull
            }?.takeIf(String::isNotBlank)
            if (next == null || !seenCursors.add(next)) break
            cursor = next
        }
        if (kind == ProviderKind.OPENAI_COMPATIBLE && openRouter) {
            val imageEndpoint = (
                configuredProvider?.let { ProviderEndpointResolver.resolve(it, ProviderEndpointKey.IMAGE_MODELS) }
                    ?: "$baseUrl/images/models"
            ).toHttpUrl()
            val imageBody = try {
                fetchPage(kind, imageEndpoint, apiKey, customHeaders, largeCatalog = true)
            } catch (error: ProviderHttpException) {
                if (error.status in setOf(404, 405)) null else throw error
            }
            collected += parseDataModels(
                imageBody?.get("data") as? JsonArray,
                baseUrl,
                openRouterOverride = true,
                officialOpenAiOverride = false,
            )
        }
        val distinct = mergeDiscoveredModels(collected)
            .sortedBy { it.displayName.lowercase() }
            .take(MAX_MODELS)
        val detailed = if (
            kind == ProviderKind.OPENAI_COMPATIBLE &&
            !openRouter &&
            !officialOpenAi &&
            !openCodeGo &&
            !openCodeZen &&
            !qwenCloud
        ) {
            enrichOpenAiCompatibleModelDetails(
                models = distinct,
                modelListUrl = modelListUrl,
                baseUrlForParsing = baseUrl,
                apiKey = apiKey,
                customHeaders = customHeaders,
            )
        } else {
            distinct
        }
        val merged = if (kind == ProviderKind.OPENAI_COMPATIBLE) {
            val withOfficialOpenAi = ModelRequestPolicy.mergeOfficialOpenAiCatalog(
                baseUrl,
                detailed,
                force = officialOpenAi,
            )
            val withOpenCodeMetadata = if (openCodeGo || openCodeZen) {
                withOfficialOpenAi.map { model ->
                    if (configuredProvider != null) {
                        ModelRequestPolicy.enrichOpenCodeModel(configuredProvider, model)
                    } else {
                        ModelRequestPolicy.enrichOpenCodeModel(providerId, baseUrl, model)
                    }
                }
            } else {
                withOfficialOpenAi
            }
            if (qwenCloud) {
                ModelRequestPolicy.mergeQwenCloudCatalog(providerId ?: "qwen-cloud", withOpenCodeMetadata)
            } else {
                withOpenCodeMetadata
            }
        } else distinct
        merged.ifEmpty { throw IllegalStateException("The provider returned no usable models") }
    }

    private suspend fun discoverOpenCodeV2(
        provider: ProviderEntity,
        apiKey: String,
    ): List<DiscoveredModel> {
        val endpoint = ProviderEndpointResolver.resolve(provider, ProviderEndpointKey.MODELS)
        val customHeaders = parseHeaders(provider.customHeadersJson)
        val request = Request.Builder().url(endpoint).get().apply {
            if (apiKey.isNotBlank()) header("Authorization", "Bearer $apiKey")
            customHeaders.forEach { (name, value) -> header(name, value) }
        }.build()
        val root = client.newCall(request).useCancellable { response ->
            if (!response.isSuccessful) {
                val detail = response.body?.readErrorSnippet()?.trim().orEmpty()
                throw ProviderHttpException(
                    response.code,
                    "OpenCode V2 model discovery failed (${response.code}): " +
                        detail.take(1_000).ifBlank { response.message },
                )
            }
            val body = response.body ?: throw IllegalStateException("OpenCode V2 returned an empty model list")
            val source = body.source()
            source.request(MAX_OPENROUTER_DISCOVERY_BYTES + 1L)
            require(source.buffer.size <= MAX_OPENROUTER_DISCOVERY_BYTES) {
                "The OpenCode V2 model list is unexpectedly large"
            }
            ProviderJson.parseToJsonElement(source.buffer.readUtf8()).jsonObject
        }
        val values = root["data"] as? JsonArray
            ?: throw ProviderProtocolException("OpenCode V2 model response did not contain data")
        return parseOpenCodeV2Models(values)
            .sortedBy { it.displayName.lowercase() }
            .take(MAX_MODELS)
            .ifEmpty { throw IllegalStateException("OpenCode V2 returned no usable models") }
    }

    internal fun parseOpenCodeV2Models(values: JsonArray?): List<DiscoveredModel> =
        values.orEmpty().mapNotNull { element ->
            val model = element as? JsonObject ?: return@mapNotNull null
            if (model["enabled"]?.jsonPrimitive?.booleanOrNull == false) return@mapNotNull null
            if (model["status"]?.jsonPrimitive?.contentOrNull.equals("deprecated", ignoreCase = true)) return@mapNotNull null
            val providerId = model["providerID"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
            val modelId = (
                model["modelID"]?.jsonPrimitive?.contentOrNull
                    ?: model["id"]?.jsonPrimitive?.contentOrNull
            )?.trim().orEmpty()
            if (providerId.isBlank() || modelId.isBlank()) return@mapNotNull null
            val capabilities = model["capabilities"] as? JsonObject
            val compatibility = model["compatibility"] as? JsonObject
            val limit = model["limit"] as? JsonObject
            val costs = (model["cost"] as? JsonArray)
                ?.mapNotNull { it as? JsonObject }
                .orEmpty()
            val cost = costs.firstOrNull { it["tier"] == null } ?: costs.firstOrNull()
            val cache = cost?.get("cache") as? JsonObject
            val input = capabilities.stringSet("input")
            val output = capabilities.stringSet("output")
            val releasedRaw = (model["time"] as? JsonObject)
                ?.get("released")?.jsonPrimitive?.longOrNull ?: 0L
            val releasedSeconds = if (releasedRaw > 10_000_000_000L) releasedRaw / 1_000L else releasedRaw
            val family = model["family"]?.jsonPrimitive?.contentOrNull.orEmpty()
            val status = model["status"]?.jsonPrimitive?.contentOrNull.orEmpty()
            DiscoveredModel(
                id = "$providerId/$modelId",
                displayName = model["name"]?.jsonPrimitive?.contentOrNull?.ifBlank { modelId } ?: modelId,
                contextWindow = limit?.get("context")?.jsonPrimitive?.intOrNull,
                maxOutputTokens = limit?.get("output")?.jsonPrimitive?.intOrNull,
                // The current Turp OpenCode V2 transport uses stateless text generation.
                // Do not advertise controls it does not yet bridge into that API.
                supportsThinking = false,
                supportsVision = false,
                supportsFiles = false,
                supportsTools = false,
                supportsImageGeneration = false,
                description = buildString {
                    if (family.isNotBlank()) append(family)
                    if (status.isNotBlank()) {
                        if (isNotEmpty()) append(" • ")
                        append(status)
                    }
                    if (input.isNotEmpty() || output.isNotEmpty()) {
                        if (isNotEmpty()) append(" • ")
                        append("server capabilities available")
                    }
                    if (compatibility?.get("reasoningField") != null) {
                        if (isNotEmpty()) append(" • ")
                        append("reasoning-capable upstream model")
                    }
                },
                createdAtEpochSeconds = releasedSeconds,
                inputCacheHitUsdPerMillion = cache?.get("read")?.jsonPrimitive?.doubleOrNull,
                inputCacheMissUsdPerMillion = cost?.get("input")?.jsonPrimitive?.doubleOrNull,
                outputUsdPerMillion = cost?.get("output")?.jsonPrimitive?.doubleOrNull,
                metadataSource = "OpenCode V2",
            )
        }

    private suspend fun enrichOpenAiCompatibleModelDetails(
        models: List<DiscoveredModel>,
        modelListUrl: String,
        baseUrlForParsing: String,
        apiKey: String,
        customHeaders: Map<String, String>,
    ): List<DiscoveredModel> {
        if (models.isEmpty()) return models
        val listEndpoint = runCatching { modelListUrl.toHttpUrl() }.getOrNull() ?: return models
        if (!listEndpoint.pathSegments.lastOrNull().orEmpty().equals("models", ignoreCase = true)) return models

        val limiter = Semaphore(MODEL_DETAIL_CONCURRENCY)
        return coroutineScope {
            models.map { base ->
                async {
                    limiter.withPermit {
                        val endpoint = listEndpoint.newBuilder()
                            .addPathSegment(base.id)
                            .build()
                        val root = runCatching {
                            fetchPage(
                                kind = ProviderKind.OPENAI_COMPATIBLE,
                                endpoint = endpoint,
                                apiKey = apiKey,
                                customHeaders = customHeaders,
                            )
                        }.getOrNull() ?: return@withPermit base
                        val detailObjects = when (val data = root["data"]) {
                            is JsonObject -> listOf(data)
                            is JsonArray -> data.mapNotNull { it as? JsonObject }
                            else -> listOf(root)
                        }
                        val parsedDetails = parseDataModels(
                            JsonArray(detailObjects),
                            baseUrlForParsing,
                            openRouterOverride = false,
                            officialOpenAiOverride = false,
                        )
                        val detail = parsedDetails.firstOrNull { it.id == base.id }
                            ?: parsedDetails.firstOrNull()
                            ?: return@withPermit base
                        mergeAuthoritativeModelDetail(base, detail)
                    }
                }
            }.awaitAll()
        }
    }

    private fun mergeAuthoritativeModelDetail(
        base: DiscoveredModel,
        detail: DiscoveredModel,
    ): DiscoveredModel = base.copy(
        displayName = base.displayName.ifBlank { detail.displayName },
        contextWindow = detail.contextWindow ?: base.contextWindow,
        maxOutputTokens = detail.maxOutputTokens ?: base.maxOutputTokens,
        supportsThinking = detail.supportsThinking ?: base.supportsThinking,
        supportsVision = detail.supportsVision ?: base.supportsVision,
        supportsFiles = detail.supportsFiles ?: base.supportsFiles,
        supportsTools = detail.supportsTools ?: base.supportsTools,
        supportsImageGeneration = detail.supportsImageGeneration ?: base.supportsImageGeneration,
        description = detail.description.ifBlank { base.description },
        createdAtEpochSeconds = detail.createdAtEpochSeconds.takeIf { it > 0 } ?: base.createdAtEpochSeconds,
        inputCacheHitUsdPerMillion = detail.inputCacheHitUsdPerMillion ?: base.inputCacheHitUsdPerMillion,
        inputCacheMissUsdPerMillion = detail.inputCacheMissUsdPerMillion ?: base.inputCacheMissUsdPerMillion,
        outputUsdPerMillion = detail.outputUsdPerMillion ?: base.outputUsdPerMillion,
        reasoningMetadataAvailable = detail.reasoningMetadataAvailable || base.reasoningMetadataAvailable,
        reasoningEfforts = if (detail.reasoningMetadataAvailable) detail.reasoningEfforts else base.reasoningEfforts,
        reasoningDefaultEffort = if (detail.reasoningMetadataAvailable) {
            detail.reasoningDefaultEffort
        } else {
            base.reasoningDefaultEffort
        },
        reasoningDefaultEnabled = if (detail.reasoningMetadataAvailable) {
            detail.reasoningDefaultEnabled
        } else {
            base.reasoningDefaultEnabled
        },
        reasoningMandatory = if (detail.reasoningMetadataAvailable) detail.reasoningMandatory else base.reasoningMandatory,
        reasoningSupportsMaxTokens = if (detail.reasoningMetadataAvailable) {
            detail.reasoningSupportsMaxTokens
        } else {
            base.reasoningSupportsMaxTokens
        },
        metadataSource = "Provider model detail",
    )

    private fun mergeDiscoveredModels(models: List<DiscoveredModel>): List<DiscoveredModel> {
        fun mergeCapability(base: Boolean?, candidate: Boolean?): Boolean? = when {
            base == true || candidate == true -> true
            candidate != null -> candidate
            else -> base
        }
        val merged = linkedMapOf<String, DiscoveredModel>()
        models.forEach { candidate ->
            val base = merged[candidate.id]
            merged[candidate.id] = if (base == null) candidate else base.copy(
                displayName = base.displayName.ifBlank { candidate.displayName },
                contextWindow = base.contextWindow ?: candidate.contextWindow,
                maxOutputTokens = base.maxOutputTokens ?: candidate.maxOutputTokens,
                supportsThinking = mergeCapability(base.supportsThinking, candidate.supportsThinking),
                supportsVision = mergeCapability(base.supportsVision, candidate.supportsVision),
                supportsFiles = mergeCapability(base.supportsFiles, candidate.supportsFiles),
                supportsTools = mergeCapability(base.supportsTools, candidate.supportsTools),
                supportsImageGeneration = mergeCapability(base.supportsImageGeneration, candidate.supportsImageGeneration),
                description = base.description.ifBlank { candidate.description },
                createdAtEpochSeconds = maxOf(base.createdAtEpochSeconds, candidate.createdAtEpochSeconds),
                inputCacheHitUsdPerMillion = base.inputCacheHitUsdPerMillion ?: candidate.inputCacheHitUsdPerMillion,
                inputCacheMissUsdPerMillion = base.inputCacheMissUsdPerMillion ?: candidate.inputCacheMissUsdPerMillion,
                outputUsdPerMillion = base.outputUsdPerMillion ?: candidate.outputUsdPerMillion,
                reasoningMetadataAvailable = candidate.reasoningMetadataAvailable || base.reasoningMetadataAvailable,
                reasoningEfforts = if (base.reasoningMetadataAvailable) base.reasoningEfforts else candidate.reasoningEfforts,
                reasoningDefaultEffort = base.reasoningDefaultEffort ?: candidate.reasoningDefaultEffort,
                reasoningDefaultEnabled = if (base.reasoningMetadataAvailable) base.reasoningDefaultEnabled else candidate.reasoningDefaultEnabled,
                reasoningMandatory = if (base.reasoningMetadataAvailable) base.reasoningMandatory else candidate.reasoningMandatory,
                reasoningSupportsMaxTokens = if (base.reasoningMetadataAvailable) base.reasoningSupportsMaxTokens else candidate.reasoningSupportsMaxTokens,
                metadataSource = base.metadataSource.ifBlank { candidate.metadataSource },
            )
        }
        return merged.values.toList()
    }

    private suspend fun fetchPage(
        kind: ProviderKind,
        endpoint: HttpUrl,
        apiKey: String,
        customHeaders: Map<String, String>,
        largeCatalog: Boolean = false,
    ): JsonObject {
        val request = Request.Builder().url(endpoint).get().apply {
            when (kind) {
                ProviderKind.OPENAI_COMPATIBLE -> if (apiKey.isNotBlank()) header("Authorization", "Bearer $apiKey")
                ProviderKind.OPENAI_OAUTH -> error("OAuth discovery is handled by OpenAiOAuthManager")
                ProviderKind.ANTHROPIC -> {
                    if (apiKey.isNotBlank()) header("x-api-key", apiKey)
                    header("anthropic-version", "2023-06-01")
                }
                ProviderKind.GEMINI -> if (apiKey.isNotBlank()) header("x-goog-api-key", apiKey)
            }
            customHeaders.forEach { (name, value) -> header(name, value) }
        }.build()
        return client.newCall(request).useCancellable { response ->
            if (!response.isSuccessful) {
                val detail = response.body?.readErrorSnippet()?.trim().orEmpty()
                val safeDetail = if (response.code in setOf(401, 403)) response.message else detail.take(1_000).ifBlank { response.message }
                throw ProviderHttpException(response.code, "Model discovery failed (${response.code}): $safeDetail")
            }
            val responseBody = response.body ?: throw IllegalStateException("The provider returned an empty model list")
            val source = responseBody.source()
            val limit = if (largeCatalog) MAX_OPENROUTER_DISCOVERY_BYTES else MAX_DISCOVERY_BYTES
            source.request(limit + 1L)
            require(source.buffer.size <= limit) { "The provider's model list is unexpectedly large" }
            ProviderJson.parseToJsonElement(source.buffer.readUtf8()).jsonObject
        }
    }

    internal fun parseDataModels(
        values: JsonArray?,
        baseUrlForParsing: String,
        openRouterOverride: Boolean? = null,
        officialOpenAiOverride: Boolean? = null,
    ): List<DiscoveredModel> = values.orEmpty().mapNotNull { element ->
        val model = element as? JsonObject ?: return@mapNotNull null
        val id = model["id"]?.jsonPrimitive?.contentOrNull?.trim().orEmpty()
        if (id.isBlank()) return@mapNotNull null
        val name = model["display_name"]?.jsonPrimitive?.contentOrNull
            ?: model["displayName"]?.jsonPrimitive?.contentOrNull
            ?: model["name"]?.jsonPrimitive?.contentOrNull
            ?: humanize(id)
        val openRouter = openRouterOverride ?: ModelRequestPolicy.isOpenRouterBaseUrl(baseUrlForParsing)
        val architecture = model["architecture"] as? JsonObject
        val inputModalities = architecture.stringSet("input_modalities")
        val outputModalities = architecture.stringSet("output_modalities")
        if (openRouter && outputModalities.isNotEmpty() && outputModalities.none { it == "text" || it == "image" }) return@mapNotNull null
        val supportedParameters = model.stringSet("supported_parameters")
        val topProvider = model["top_provider"] as? JsonObject
        val pricing = model["pricing"] as? JsonObject
        val reasoning = model["reasoning"] as? JsonObject
        val efforts = reasoning?.stringSet("supported_efforts").orEmpty().mapNotNull(::parseThinkingEffort)
        DiscoveredModel(
            id = id,
            displayName = name,
            contextWindow = model.int(
                "context_length",
                "context_window",
                "contextWindow",
                "inputTokenLimit",
                "max_model_len",
                "max_sequence_length",
            ) ?: (model["capabilities"] as? JsonObject)?.int(
                "context_length",
                "context_window",
                "contextWindow",
                "max_model_len",
            ) ?: topProvider?.int("context_length", "context_window"),
            maxOutputTokens = model.int(
                "outputTokenLimit",
                "max_output_tokens",
                "maxOutputTokens",
                "max_completion_tokens",
            ) ?: (model["capabilities"] as? JsonObject)?.int(
                "outputTokenLimit",
                "max_output_tokens",
                "maxOutputTokens",
                "max_completion_tokens",
            ) ?: topProvider?.int("max_completion_tokens", "max_output_tokens"),
            supportsThinking = when {
                reasoning != null || "reasoning" in supportedParameters -> true
                else -> model["thinking"]?.jsonPrimitive?.booleanOrNull
            },
            supportsVision = if (openRouter) {
                "image" in inputModalities
            } else {
                model.booleanCapability("supports_vision", "supportsVision", "vision", "vision_enabled")
                    ?: ("image" in inputModalities).takeIf { inputModalities.isNotEmpty() }
            },
            supportsFiles = if (openRouter) {
                "file" in inputModalities
            } else {
                model.booleanCapability("supports_files", "supportsFiles", "files", "file_uploads")
                    ?: ("file" in inputModalities).takeIf { inputModalities.isNotEmpty() }
            },
            supportsTools = if (openRouter) {
                "tools" in supportedParameters
            } else {
                model.booleanCapability(
                    "supports_tools",
                    "supportsTools",
                    "tools",
                    "tool_calling",
                    "function_calling",
                ) ?: (
                    "tools" in supportedParameters ||
                        "tool_choice" in supportedParameters ||
                        "function_calling" in supportedParameters ||
                        "functions" in supportedParameters
                    ).takeIf { supportedParameters.isNotEmpty() }
            },
            supportsImageGeneration = model.booleanCapability("supports_image_generation", "supportsImageGeneration", "image_generation") ?: when {
                openRouter -> "image" in outputModalities
                outputModalities.isNotEmpty() -> "image" in outputModalities
                (officialOpenAiOverride ?: ModelRequestPolicy.isOfficialOpenAiBaseUrl(baseUrlForParsing)) -> imageGenerationModelHeuristic(id)
                else -> null
            },
            description = model["description"]?.jsonPrimitive?.contentOrNull.orEmpty(),
            createdAtEpochSeconds = model["created"]?.jsonPrimitive?.longOrNull ?: 0,
            inputCacheHitUsdPerMillion = pricing.pricePerMillion("input_cache_read"),
            inputCacheMissUsdPerMillion = pricing.pricePerMillion("prompt"),
            outputUsdPerMillion = pricing.pricePerMillion("completion"),
            reasoningMetadataAvailable = reasoning != null,
            reasoningEfforts = efforts,
            reasoningDefaultEffort = reasoning?.get("default_effort")?.jsonPrimitive?.contentOrNull?.let(::parseThinkingEffort),
            reasoningDefaultEnabled = reasoning?.get("default_enabled")?.jsonPrimitive?.booleanOrNull ?: false,
            reasoningMandatory = reasoning?.get("mandatory")?.jsonPrimitive?.booleanOrNull ?: false,
            reasoningSupportsMaxTokens = reasoning?.get("supports_max_tokens")?.jsonPrimitive?.booleanOrNull ?: false,
            metadataSource = if (openRouter) "OpenRouter" else "",
        )
    }

    internal fun parseGeminiModels(values: JsonArray?): List<DiscoveredModel> = values.orEmpty().mapNotNull { element ->
        val model = element as? JsonObject ?: return@mapNotNull null
        val methods = (model["supportedGenerationMethods"] as? JsonArray)
            ?.mapNotNull { it.jsonPrimitive.contentOrNull }
            .orEmpty()
        if (methods.isNotEmpty() && "generateContent" !in methods) return@mapNotNull null
        val id = model["name"]?.jsonPrimitive?.contentOrNull?.removePrefix("models/")?.trim().orEmpty()
        if (id.isBlank()) return@mapNotNull null
        val name = model["displayName"]?.jsonPrimitive?.contentOrNull ?: humanize(id)
        val imageGeneration = geminiImageGenerationModelHeuristic(id, name)
        DiscoveredModel(
            id = id,
            displayName = name,
            contextWindow = model["inputTokenLimit"]?.jsonPrimitive?.intOrNull,
            maxOutputTokens = model["outputTokenLimit"]?.jsonPrimitive?.intOrNull,
            supportsThinking = model["thinking"]?.jsonPrimitive?.booleanOrNull,
            supportsVision = if (imageGeneration) true else model.booleanCapability("supports_vision", "supportsVision", "vision"),
            supportsImageGeneration = imageGeneration,
            description = model["description"]?.jsonPrimitive?.contentOrNull.orEmpty(),
            metadataSource = "Gemini API",
        )
    }

    internal fun geminiImageGenerationModelHeuristic(id: String, displayName: String = ""): Boolean {
        val normalized = id.substringAfterLast('/').lowercase()
        val display = displayName.lowercase()
        return normalized.startsWith("imagen-") ||
            normalized.contains("image-generation") ||
            normalized.endsWith("-image") ||
            normalized.contains("-image-") ||
            display.contains("image generation") ||
            display.contains("nano banana")
    }

    private fun JsonObject.booleanCapability(vararg names: String): Boolean? {
        names.forEach { name -> this[name]?.jsonPrimitive?.booleanOrNull?.let { return it } }
        val capabilities = this["capabilities"] as? JsonObject ?: return null
        names.forEach { name -> capabilities[name]?.jsonPrimitive?.booleanOrNull?.let { return it } }
        return null
    }

    // OpenRouter advertises catalog pricing as USD per token. Turp persists model
    // pricing as USD per million tokens, so fractional token prices are normalized here.
    private fun JsonObject?.pricePerMillion(name: String): Double? = this?.get(name)
        ?.jsonPrimitive?.doubleOrNull?.takeIf { it >= 0.0 }?.times(TOKENS_PER_MILLION)

    private fun JsonObject.int(vararg names: String): Int? {
        names.forEach { name -> this[name]?.jsonPrimitive?.intOrNull?.let { return it } }
        return null
    }

    private fun JsonObject?.stringSet(name: String): Set<String> =
        (this?.get(name) as? JsonArray)?.mapNotNull { it.jsonPrimitive.contentOrNull?.lowercase() }?.toSet().orEmpty()

    private fun parseThinkingEffort(raw: String): ThinkingEffort? = when (raw.trim().lowercase()) {
        "minimal" -> ThinkingEffort.MINIMAL
        "low" -> ThinkingEffort.LOW
        "medium" -> ThinkingEffort.MEDIUM
        "high" -> ThinkingEffort.HIGH
        "xhigh" -> ThinkingEffort.XHIGH
        "max" -> ThinkingEffort.MAX
        else -> null
    }

    private fun humanize(id: String): String = id.substringAfterLast('/').replace('-', ' ').replace('_', ' ')
        .split(' ').filter(String::isNotBlank).joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }

    internal fun imageGenerationModelHeuristic(id: String): Boolean {
        val normalized = id.substringAfterLast('/').lowercase()
        return normalized.startsWith("gpt-image-") ||
            normalized.startsWith("dall-e-") ||
            normalized.startsWith("imagen-") ||
            normalized.contains("image-generation") ||
            normalized.endsWith("-image")
    }

    private companion object {
        const val TOKENS_PER_MILLION = 1_000_000.0
        const val MAX_MODELS = 1_000
        const val MAX_PAGES = 10
        const val MODEL_DETAIL_CONCURRENCY = 8
        const val MAX_DISCOVERY_BYTES = 2L * 1024 * 1024
        const val MAX_OPENROUTER_DISCOVERY_BYTES = 12L * 1024 * 1024
    }
}
