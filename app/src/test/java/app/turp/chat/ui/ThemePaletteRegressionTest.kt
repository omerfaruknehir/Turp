package app.turp.chat.ui

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ThemePaletteRegressionTest {
    @Test
    fun `Turp uses neutral surfaces with a radish accent and Arbor preserves green`() {
        val theme = File("src/main/java/app/turp/chat/ui/theme/Theme.kt").readText()
        val preferences = File("src/main/java/app/turp/chat/settings/AppPreferences.kt").readText()
        val settings = File("src/main/java/app/turp/chat/ui/SettingsScreen.kt").readText()

        assertTrue(preferences.contains("TURP, ARBOR, SYSTEM"))

        assertTrue(theme.contains("private val TurpLight"))
        assertTrue(theme.contains("primary = Color(0xFF9F244A)"))
        assertTrue(theme.contains("background = Color(0xFFFAF9FA)"))
        assertTrue(theme.contains("secondary = Color(0xFF625D60)"))
        assertTrue(theme.contains("secondaryContainer = Color(0xFFE9E1E4)"))
        assertFalse(theme.contains("secondary = Color(0xFF5D625F)"))

        assertTrue(theme.contains("private val TurpDark"))
        assertTrue(theme.contains("primary = Color(0xFFFFB0C5)"))
        assertTrue(theme.contains("background = Color(0xFF111113)"))
        assertTrue(theme.contains("primaryContainer = Color(0xFF4A2130)"))
        assertTrue(theme.contains("secondary = Color(0xFFCDC4C7)"))
        assertTrue(theme.contains("secondaryContainer = Color(0xFF433B3E)"))
        assertFalse(theme.contains("secondary = Color(0xFFC3C8C3)"))

        assertTrue(theme.contains("private val ArborLight"))
        assertTrue(theme.contains("primary = Color(0xFF286448)"))
        assertTrue(theme.contains("private val ArborDark"))
        assertTrue(theme.contains("primary = Color(0xFF99D5B1)"))

        assertTrue(settings.contains("ColorPalette.TURP -> \"Neutral graphite surfaces with a focused radish accent\""))
        assertTrue(settings.contains("ColorPalette.ARBOR -> \"Arbor\""))
        assertTrue(
            settings.contains(
                "ColorPalette.TURP,\n        ColorPalette.SYSTEM,\n        ColorPalette.ARBOR,"
            )
        )
        assertFalse(settings.contains("classic Turp green icon"))

        val colors = File("src/main/res/values/colors.xml").readText()
        assertTrue(colors.contains("<color name=\"turp_splash_fallback\">#FAF9FA</color>"))
        assertTrue(colors.contains("<color name=\"turp_splash_system\">#FAF9FA</color>"))
    }
}
