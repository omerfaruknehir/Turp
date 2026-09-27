package app.turp.chat.provider

import app.turp.chat.data.ProviderEntity
import app.turp.chat.data.ProviderKind
import app.turp.chat.data.ProviderProfile
import app.turp.chat.data.ProviderProtocol
import java.util.Collections
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConfigurableProviderDiscoveryTest {
    @Test
    fun `generic OpenAI compatible discovery enriches models from detail endpoints`() = runBlocking {
        val seen = Collections.synchronizedList(mutableListOf<String>())
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val path = chain.request().url.encodedPath
            seen += path
            val json = when (path) {
                "/v1/models" -> """{"data":[{"id":"alpha","name":"Alpha Listed","context_length":4096},{"id":"vendor/beta","name":"Beta Listed"}]}"""
                "/v1/models/alpha" -> """{"id":"alpha","capabilities":{"context_window":131072},"max_output_tokens":8192,"thinking":true,"architecture":{"input_modalities":["text","image"],"output_modalities":["text"]},"supported_parameters":["tools"],"description":"Alpha detail"}"""
                "/v1/models/vendor%2Fbeta" -> """{"data":[{"id":"vendor/beta","contextWindow":262144,"maxOutputTokens":16384,"capabilities":{"supportsFiles":true,"function_calling":true},"description":"Beta detail"}]}"""
                else -> error("Unexpected path: $path")
            }
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(json.toResponseBody("application/json".toMediaType()))
                .build()
        }.build()
        val provider = ProviderEntity(
            id = "local-openai",
            displayName = "Local OpenAI",
            kind = ProviderKind.OPENAI_COMPATIBLE,
            baseUrl = "https://local.example.test/v1",
            protocol = ProviderProtocol.OPENAI_COMPATIBLE,
            profile = ProviderProfile.GENERIC,
            apiKeyRequired = false,
        )

        val models = ModelDiscoveryService(oauth = null, client = client).discover(provider, "")
        val alpha = models.first { it.id == "alpha" }
        val beta = models.first { it.id == "vendor/beta" }

        assertTrue(seen.contains("/v1/models"))
        assertTrue(seen.contains("/v1/models/alpha"))
        assertTrue(seen.contains("/v1/models/vendor%2Fbeta"))

        assertEquals("Alpha Listed", alpha.displayName)
        assertEquals(131072, alpha.contextWindow)
        assertEquals(8192, alpha.maxOutputTokens)
        assertEquals(true, alpha.supportsThinking)
        assertEquals(true, alpha.supportsTools)
        assertEquals(true, alpha.supportsVision)
        assertEquals("Alpha detail", alpha.description)
        assertEquals("Provider model detail", alpha.metadataSource)

        assertEquals("Beta Listed", beta.displayName)
        assertEquals(262144, beta.contextWindow)
        assertEquals(16384, beta.maxOutputTokens)
        assertEquals(true, beta.supportsFiles)
        assertEquals(true, beta.supportsTools)
        assertEquals("Beta detail", beta.description)
        assertEquals("Provider model detail", beta.metadataSource)
    }

    @Test
    fun `model detail lookup failure does not discard list model`() = runBlocking {
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val path = chain.request().url.encodedPath
            val code = if (path == "/v1/models") 200 else 404
            val json = if (path == "/v1/models") {
                """{"data":[{"id":"alpha","name":"Alpha","context_length":4096}]}"""
            } else {
                """{"error":{"message":"not found"}}"""
            }
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message(if (code == 200) "OK" else "Not Found")
                .body(json.toResponseBody("application/json".toMediaType()))
                .build()
        }.build()
        val provider = ProviderEntity(
            id = "local-openai",
            displayName = "Local OpenAI",
            kind = ProviderKind.OPENAI_COMPATIBLE,
            baseUrl = "https://local.example.test/v1",
            protocol = ProviderProtocol.OPENAI_COMPATIBLE,
            profile = ProviderProfile.GENERIC,
            apiKeyRequired = false,
        )

        val model = ModelDiscoveryService(oauth = null, client = client).discover(provider, "").single()

        assertEquals("alpha", model.id)
        assertEquals(4096, model.contextWindow)
        assertEquals("", model.metadataSource)
    }

    @Test
    fun `transient model detail failure is retried before falling back`() = runBlocking {
        var detailAttempts = 0
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val path = chain.request().url.encodedPath
            val code: Int
            val json: String
            when (path) {
                "/v1/models" -> {
                    code = 200
                    json = """{"data":[{"id":"retry-me","name":"Retry Me","task":"chat"}]}"""
                }
                "/v1/models/retry-me" -> {
                    detailAttempts += 1
                    if (detailAttempts == 1) {
                        code = 503
                        json = """{"error":{"message":"temporarily busy"}}"""
                    } else {
                        code = 200
                        json = """{"id":"retry-me","task":"chat","context_window":262144,"max_output_tokens":16384,"modalities":["text","image"]}"""
                    }
                }
                else -> error("Unexpected path: $path")
            }
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(code)
                .message(if (code == 200) "OK" else "Service Unavailable")
                .body(json.toResponseBody("application/json".toMediaType()))
                .build()
        }.build()
        val provider = ProviderEntity(
            id = "retry-compatible",
            displayName = "Retry Compatible",
            kind = ProviderKind.OPENAI_COMPATIBLE,
            baseUrl = "https://retry.example.test/v1",
            protocol = ProviderProtocol.OPENAI_COMPATIBLE,
            profile = ProviderProfile.GENERIC,
            apiKeyRequired = false,
        )

        val model = ModelDiscoveryService(oauth = null, client = client).discover(provider, "").single()

        assertEquals(2, detailAttempts)
        assertEquals(262144, model.contextWindow)
        assertEquals(16384, model.maxOutputTokens)
        assertEquals(true, model.supportsVision)
        assertEquals("Provider model detail", model.metadataSource)
    }

    @Test
    fun `generic discovery honors top level modalities and hides non chat tasks`() = runBlocking {
        val seen = Collections.synchronizedList(mutableListOf<String>())
        val tasks = mapOf(
            "evren-chat" to "chat",
            "evren-embedding" to "embedding",
            "evren-reranker" to "rerank",
            "evren-ocr" to "ocr",
            "evren-asr" to "asr",
            "evren-guard" to "guard",
        )
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val path = chain.request().url.encodedPath
            seen += path
            val json = if (path == "/v1/models") {
                """{"data":[
                    {"id":"evren-chat","name":"EVREN Chat","task":"chat","modalities":["text","image"]},
                    {"id":"evren-embedding","task":"embedding","modalities":["text"]},
                    {"id":"evren-reranker","task":"rerank","modalities":["text","image"]},
                    {"id":"evren-ocr","task":"ocr","modalities":["text","image"]},
                    {"id":"evren-asr","task":"asr","modalities":["audio","text"]},
                    {"id":"evren-guard","task":"guard","modalities":["text"]}
                ]}"""
            } else {
                val id = chain.request().url.pathSegments.last()
                val task = tasks.getValue(id)
                """{"id":"$id","task":"$task","modalities":["text","image"]}"""
            }
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(json.toResponseBody("application/json".toMediaType()))
                .build()
        }.build()
        val provider = ProviderEntity(
            id = "evren-compatible",
            displayName = "EVREN",
            kind = ProviderKind.OPENAI_COMPATIBLE,
            baseUrl = "https://evren.example.test/v1",
            protocol = ProviderProtocol.OPENAI_COMPATIBLE,
            profile = ProviderProfile.GENERIC,
            apiKeyRequired = false,
        )

        val models = ModelDiscoveryService(oauth = null, client = client).discover(provider, "")

        assertEquals(listOf("evren-chat"), models.map { it.id })
        assertEquals("chat", models.single().task)
        assertEquals(true, models.single().supportsVision)
        assertTrue(seen.contains("/v1/models/evren-chat"))
        assertTrue(seen.contains("/v1/models/evren-ocr"))
        assertTrue(seen.contains("/v1/models/evren-asr"))
    }

    @Test
    fun `proxied OpenRouter discovery uses configured profile and endpoint paths`() = runBlocking {
        val seen = mutableListOf<String>()
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            seen += chain.request().url.encodedPath
            val json = when (chain.request().url.encodedPath) {
                "/router/catalog" -> """{"data":[{"id":"vendor/chat","name":"Chat","architecture":{"input_modalities":["text"],"output_modalities":["text"]},"pricing":{"prompt":"0.0000015","completion":"0.000004"}}]}"""
                "/router/image-catalog" -> """{"data":[]}"""
                else -> error("Unexpected path: " + chain.request().url.encodedPath)
            }
            Response.Builder()
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body(json.toResponseBody("application/json".toMediaType()))
                .build()
        }.build()
        val provider = ProviderEntity(
            id = "proxy-router",
            displayName = "Proxy Router",
            kind = ProviderKind.OPENAI_COMPATIBLE,
            baseUrl = "https://proxy.example.test/router",
            protocol = ProviderProtocol.OPENAI_COMPATIBLE,
            profile = ProviderProfile.OPENROUTER,
            endpointOverridesJson = """{"models":"catalog","imageModels":"image-catalog"}""",
        )

        val models = ModelDiscoveryService(oauth = null, client = client).discover(provider, "key")

        assertEquals(listOf("/router/catalog", "/router/image-catalog"), seen)
        val model = models.single()
        assertEquals("vendor/chat", model.id)
        assertEquals(1.5, model.inputCacheMissUsdPerMillion ?: -1.0, 0.000001)
        assertEquals(4.0, model.outputUsdPerMillion ?: -1.0, 0.000001)
        assertEquals("OpenRouter", model.metadataSource)
        assertTrue(ModelRequestPolicy.isOpenRouter(provider))
    }
}
