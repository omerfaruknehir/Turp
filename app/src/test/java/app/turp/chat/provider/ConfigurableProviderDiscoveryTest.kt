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
                "/v1/models/alpha" -> """{"id":"alpha","context_window":131072,"max_output_tokens":8192,"thinking":true,"capabilities":{"supports_tools":true,"supports_vision":true},"description":"Alpha detail"}"""
                "/v1/models/vendor%2Fbeta" -> """{"data":{"id":"vendor/beta","contextWindow":262144,"maxOutputTokens":16384,"capabilities":{"supportsFiles":true,"function_calling":true},"description":"Beta detail"}}"""
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
