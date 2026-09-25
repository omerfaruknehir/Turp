package app.turp.chat.provider

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.turp.chat.data.ProviderEntity
import app.turp.chat.data.ProviderKind
import app.turp.chat.data.ProviderProfile
import app.turp.chat.data.ProviderProtocol
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProviderEndpointResolverAndroidTest {
    @Test
    fun endpoint_template_validation_uses_android_safe_regex() {
        val provider = ProviderEntity(
            id = "android-regex",
            displayName = "Android regex",
            kind = ProviderKind.OPENAI_COMPATIBLE,
            baseUrl = "https://proxy.example.test/v1",
            protocol = ProviderProtocol.OPENAI_COMPATIBLE,
            profile = ProviderProfile.GENERIC,
            endpointOverridesJson = """{"geminiStream":"models/{model}:streamGenerateContent?alt=sse"}""",
        )

        assertEquals(
            "https://proxy.example.test/v1/models/gemini-test:streamGenerateContent?alt=sse",
            ProviderEndpointResolver.resolve(
                provider,
                ProviderEndpointKey.GEMINI_STREAM,
                mapOf("model" to "gemini-test"),
            ),
        )
    }
}
