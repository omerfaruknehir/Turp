package app.turp.chat

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ReleaseVersionRegressionTest {
    private fun repositoryFile(path: String): File = sequenceOf(File(path), File("..", path))
        .firstOrNull(File::isFile)
        ?: error("Could not locate repository file: $path")

    @Test
    fun `release metadata is scoped to 0_25_1`() {
        val gradle = repositoryFile("app/build.gradle.kts").readText()
        val english = repositoryFile("docs/releases/RELEASE_NOTES_0.25.1.md").readText()
        val turkish = repositoryFile("docs/releases/tr/RELEASE_NOTES_0.25.1.md").readText()

        assertTrue(gradle.contains("versionCode = 221"))
        assertTrue(gradle.contains("versionName = \"0.25.1\""))
        assertTrue(english.startsWith("# Turp 0.25.1"))
        assertTrue(turkish.startsWith("# Turp 0.25.1"))

        val settings = repositoryFile("app/src/main/java/app/turp/chat/ui/SettingsScreen.kt").readText()
        assertTrue(settings.contains("\"Trup is upper to date!?\""))
        assertTrue(settings.contains("\"Trup daha güncel!?\""))
        assertTrue(settings.contains("configuration.locales[0].language.equals(\"tr\""))
    }
}
