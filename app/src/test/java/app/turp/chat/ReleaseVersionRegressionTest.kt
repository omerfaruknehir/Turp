package app.turp.chat

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ReleaseVersionRegressionTest {
    private fun repositoryFile(path: String): File = sequenceOf(File(path), File("..", path))
        .firstOrNull(File::isFile)
        ?: error("Could not locate repository file: $path")

    @Test
    fun `release metadata is scoped to 0_25_0`() {
        val gradle = repositoryFile("app/build.gradle.kts").readText()
        val english = repositoryFile("docs/releases/RELEASE_NOTES_0.25.0.md").readText()
        val turkish = repositoryFile("docs/releases/tr/RELEASE_NOTES_0.25.0.md").readText()

        assertTrue(gradle.contains("versionCode = 220"))
        assertTrue(gradle.contains("versionName = \"0.25.0\""))
        assertTrue(english.startsWith("# Turp 0.25.0"))
        assertTrue(turkish.startsWith("# Turp 0.25.0"))
    }
}
