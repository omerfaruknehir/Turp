package app.turp.chat.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class TurpMarkTest {
    @Test
    fun `in app Turp marks reuse exact launcher artwork`() {
        val visuals = File("src/main/java/app/turp/chat/ui/PaletteVisuals.kt").readText()
        assertTrue(visuals.contains("LocalTurpIconPalette"))
        assertTrue(visuals.contains("painterResource(palette.turpMarkDrawable)"))
        assertTrue(visuals.contains("painterResource(palette.launcherBackgroundDrawable)"))
        assertTrue(visuals.contains("painterResource(palette.launcherForegroundDrawable)"))
        assertTrue(visuals.contains("ColorPalette.ARBOR -> R.drawable.ic_turp_mark_arbor"))
        assertTrue(visuals.contains("ColorPalette.ARBOR -> R.drawable.ic_turp_background_arbor"))
        assertTrue(visuals.contains("ColorPalette.ARBOR -> R.drawable.ic_turp_foreground_arbor"))
        assertTrue(visuals.contains("R.drawable.ic_turp_background_ocean"))
        assertTrue(visuals.contains("R.drawable.ic_turp_foreground_ocean"))
        assertTrue(visuals.contains("22f / 108f"))
        assertFalse(visuals.contains("launcherIconDrawable"))
        assertFalse(visuals.contains("R.mipmap.ic_launcher"))
        assertFalse(visuals.contains("Canvas("))
        assertFalse(visuals.contains("drawPath("))

        val main = File("src/main/java/app/turp/chat/MainActivity.kt").readText()
        assertTrue(main.contains("LocalTurpIconPalette provides"))
        assertTrue(main.contains("matchLauncherIconToPalette"))
    }

    @Test
    fun `drawer onboarding and licenses share the dynamic Turp mark`() {
        val onboarding = File("src/main/java/app/turp/chat/ui/OnboardingScreen.kt").readText()
        val sidebar = File("src/main/java/app/turp/chat/ui/ConversationSidebar.kt").readText()
        val licenses = File("src/main/java/app/turp/chat/ui/LicenseCatalogScreen.kt").readText()
        assertTrue(onboarding.contains("TurpMark("))
        assertTrue(sidebar.contains("TurpMark("))
        assertTrue(licenses.contains("component.id == \"turp\""))
        assertTrue(licenses.contains("TurpMark("))
    }
}
