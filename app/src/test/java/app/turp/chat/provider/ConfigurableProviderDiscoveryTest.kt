package app.turp.chat.provider

import app.turp.chat.data.ProviderEntity
import app.turp.chat.data.ProviderKind
import app.turp.chat.data.ProviderProfile
import app.turp.chat.data.ProviderProtocol
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
