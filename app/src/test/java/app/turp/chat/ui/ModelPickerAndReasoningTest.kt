package app.turp.chat.ui

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color
import app.turp.chat.data.ModelEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelPickerAndReasoningTest {
    @Test
    fun imageModelsRemainInNormalPicker() {
        val chat = model("gpt-4.1", "GPT-4.1", false)
        val image = model("gpt-image-1", "GPT Image 1", true)
        val picker = normalModelPickerModels(listOf(image, chat))

        assertEquals(2, picker.size)
        assertTrue(picker.any { it.modelId == "gpt-image-1" && it.supportsImageGeneration })
    }

    @Test
    fun imageModelsAreNotFreeWhenOnlyTextTokenPricesAreZero() {
        val image = model("image", "Image", true).copy(pricingConfigured = true)
        val chat = model("chat", "Chat", false).copy(pricingConfigured = true)

        assertFalse(image.isActuallyFree)
        assertTrue(chat.isActuallyFree)
    }

    @Test
    fun websiteLinksCarryTheResolvedAppColorScheme() {
        val url = turpWebsiteUrl(
            "privacy/",
            darkColorScheme(
                primary = Color(0xFF99D5B1),
                background = Color(0xFF101411),
                onSurface = Color(0xFFDFE4DF),
            ),
        )

        assertTrue(url.startsWith("https://omerfaruknehir.github.io/Turp/privacy/?theme=app&dark=1"))
        assertTrue(url.contains("primary=99d5b1"))
        assertTrue(url.contains("background=101411"))
        assertTrue(url.contains("onSurface=dfe4df"))
    }

    @Test
    fun capabilityNoticesOnlyAppearForUnsupportedRequestedFeatures() {
        assertFalse(shouldShowOcrCompatibility(isImage = true, modelSupportsVision = true))
        assertFalse(shouldShowOcrCompatibility(isImage = false, modelSupportsVision = false))
        assertTrue(shouldShowOcrCompatibility(isImage = true, modelSupportsVision = false))

        assertEquals(null, unsupportedToolCallingNotice(modelSupportsTools = true, toolCallingRequested = true))
        assertEquals(null, unsupportedToolCallingNotice(modelSupportsTools = false, toolCallingRequested = false))
        val ordinaryNotice = unsupportedToolCallingNotice(
            modelSupportsTools = false,
            toolCallingRequested = true,
        ).orEmpty()
        assertTrue(ordinaryNotice.contains("Provider/catalog metadata reports"))
        assertFalse(ordinaryNotice.startsWith("This model doesn't support"))

        val sudoNotice = unsupportedToolCallingNotice(
            modelSupportsTools = false,
            toolCallingRequested = true,
            sudoNativeAttempt = true,
        ).orEmpty()
        assertTrue(sudoNotice.contains("Sudo will still try real native tool definitions"))
        assertTrue(sudoNotice.contains("provider/API may reject"))
        assertFalse(sudoNotice.contains("won't run"))

        assertTrue(
            shouldOfferToolFallbackForError(
                "The provider/API endpoint rejected a request that included Turp's native tool definitions.",
            ),
        )
        assertTrue(
            shouldOfferToolFallbackForError(
                "Enable Tool-call fallback for this model in Turp.",
            ),
        )
        assertFalse(shouldOfferToolFallbackForError("Ordinary network failure"))
    }

    @Test
    fun toolsFilterIsExplicitlyMetadataBased() {
        assertEquals("Reported tools", ModelPickerFilter.TOOLS.label)
    }

    @Test
    fun reasoningMarkdownKeepsStructuredMarkdownAndFences() {
        val markdown = """# Heading

**bold** and `inline`

| A | B |
|---|---|
| 1 | 2 |

> quote

```python
print("must not run")
```

```turp-widget
{"type":"text"}
```
"""
        val blocks = parseBlocks(markdown, streaming = false)
        assertTrue(blocks.any { it is RichBlock.Markdown && it.text.contains("# Heading") })
        assertTrue(blocks.any { it is RichBlock.Table })
        assertEquals(2, blocks.count { it is RichBlock.Code })
        assertTrue(blocks.filterIsInstance<RichBlock.Code>().all { it.complete })
    }

    @Test
    fun fencedReasoningCodeIsNeverExecutable() {
        assertFalse(shouldExecuteRichCodeBlock(displayOnly = true, complete = true))
        assertFalse(shouldExecuteRichCodeBlock(displayOnly = true, complete = false))
        assertTrue(shouldExecuteRichCodeBlock(displayOnly = false, complete = true))
    }

    private fun model(id: String, name: String, image: Boolean) = ModelEntity(
        providerId = "openai",
        modelId = id,
        displayName = name,
        contextWindow = 128_000,
        maxOutputTokens = 16_384,
        inputCacheHitUsdPerMillion = 0.0,
        inputCacheMissUsdPerMillion = 0.0,
        outputUsdPerMillion = 0.0,
        supportsImageGeneration = image,
    )
}
