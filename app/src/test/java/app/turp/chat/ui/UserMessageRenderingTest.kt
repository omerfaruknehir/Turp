package app.turp.chat.ui

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class UserMessageRenderingTest {
    @Test
    fun `completed user messages bypass streaming parser state`() {
        val chat = File("src/main/java/app/turp/chat/ui/ChatScreen.kt").readText()
        val rich = File("src/main/java/app/turp/chat/ui/RichMessage.kt").readText()
        assertTrue(chat.contains("staticContent = user"))
        assertTrue(rich.contains("val visibleBlocks = if (staticContent) staticBlocks else blocks"))
    }

    @Test
    fun `developer message source view uses exact stored content`() {
        val chat = File("src/main/java/app/turp/chat/ui/ChatScreen.kt").readText()
        val settings = File("src/main/java/app/turp/chat/ui/SettingsScreen.kt").readText()
        val preferences = File("src/main/java/app/turp/chat/settings/AppPreferences.kt").readText()

        assertTrue(settings.contains("\"Show source\""))
        assertTrue(preferences.contains("showMessageSourceEnabled"))
        assertTrue(preferences.contains("KEY_SHOW_MESSAGE_SOURCE_ENABLED"))
        assertTrue(chat.contains("developerSettings.enabled && developerSettings.showMessageSourceEnabled"))
        assertTrue(chat.contains("code = developerMessageSource("))
        assertTrue(chat.contains("content = message.content"))
        assertTrue(chat.contains("reasoning = message.reasoning"))
        assertTrue(chat.contains("[DIRECT HTTP REQUEST · REDACTED]"))
        assertTrue(chat.contains("title = \"MESSAGE SOURCE\""))
        assertTrue(chat.contains("if (sourceVisible) \"Rendered\" else \"Source\""))
    }

    @Test
    fun `completed markdown precomputes text metrics off main thread`() {
        val rich = File("src/main/java/app/turp/chat/ui/RichMessage.kt").readText()

        assertTrue(rich.contains("PrecomputedTextCompat.create(decorated, metricsParams)"))
        assertTrue(rich.contains("TextViewCompat.setPrecomputedText(view, ready.precomputed)"))
        assertTrue(rich.contains("precompute = !streaming"))
        assertTrue(rich.contains("withContext(Dispatchers.Default)"))
        assertTrue(rich.contains("decorateReferenceSpans("))
    }

    @Test
    fun `markdown view retains the parsed frame while the next snapshot parses`() {
        val rich = File("src/main/java/app/turp/chat/ui/RichMessage.kt").readText()
        assertTrue(rich.contains("mutableStateOf<PreparedMarkdownSource?>("))
        assertTrue(rich.contains("val parsed = RenderedMarkdownCache.getOrRender(markwon, markdown)"))
        assertTrue(rich.contains("val ready = preparedMarkdown"))
        assertTrue(rich.contains("if (ready == null)"))
        assertTrue(rich.contains("markdownRenderFallbackText(markdown)"))
    }
}
