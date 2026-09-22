package app.turp.chat.ui

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TurpBrandingRegressionTest {
    private fun source(path: String) = File(path).readText()

    @Test
    fun `launcher and in-app marks use same-hue Turp radish geometry`() {
        val foreground = source("src/main/res/drawable/ic_turp_foreground.xml")
        val mark = source("src/main/res/drawable/ic_turp_mark.xml")
        val monochrome = source("src/main/res/drawable/ic_turp_monochrome.xml")
        assertTrue(foreground.contains("M45.355735,12.06325"))
        assertTrue(foreground.contains("M54,24.484818"))
        assertTrue(foreground.contains("#FFC85A4B"))
        assertTrue(foreground.contains("#FFE77B6C"))
        assertFalse(foreground.contains("#FFD0A390"))
        assertFalse(foreground.contains("#FFF3E4DE"))
        assertTrue(mark.contains("#FFC85A4B"))
        assertTrue(mark.contains("#FFE77B6C"))
        assertTrue(monochrome.contains("M45.355735,12.06325"))
        assertTrue(monochrome.contains("#FFFFFFFF"))
        assertFalse(foreground.contains("M734,681"))
    }

    @Test
    fun `launcher safe-zone wrappers leave the background visibly exposed`() {
        val safe = source("src/main/res/drawable/ic_launcher_foreground_safe.xml")
        assertTrue(safe.contains("@drawable/ic_turp_foreground"))
        assertTrue(safe.contains("android:insetLeft=\"22dp\""))
        assertTrue(safe.contains("android:insetRight=\"22dp\""))
        assertTrue(safe.contains("android:insetTop=\"22dp\""))
        assertTrue(safe.contains("android:insetBottom=\"22dp\""))

        listOf("", "_arbor", "_graphite", "_ocean", "_sunset", "_system", "_violet").forEach { suffix ->
            val launcher = source("src/main/res/mipmap/ic_launcher$suffix.xml")
            assertTrue("legacy launcher $suffix scaleX", launcher.contains("android:scaleX=\".60\""))
            assertTrue("legacy launcher $suffix scaleY", launcher.contains("android:scaleY=\".60\""))
        }
    }

    @Test
    fun `display brand changes without changing package identity`() {
        val strings = source("src/main/res/values/strings.xml")
        val gradle = File("build.gradle.kts").readText()
        assertTrue(strings.contains("<string name=\"app_name\">Turp</string>"))
        assertTrue(gradle.contains("applicationId = \"app.turp.chat\""))
    }
}
