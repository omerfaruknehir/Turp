package app.turp.chat.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ToolCallFallbackSettingsTest {
    @Test
    fun perModelOverridesBeatGlobalDefault() {
        val provider = "custom"
        val enabledModel = "model-enabled"
        val disabledModel = "model-disabled"
        val enabledKey = provider + "\u0000" + enabledModel
        val disabledKey = provider + "\u0000" + disabledModel

        val settings = ToolCallFallbackSettings(
            enabledByDefault = true,
            enabledModelKeys = setOf(enabledKey),
            disabledModelKeys = setOf(disabledKey),
        )

        assertTrue(settings.isEnabled(provider, enabledModel))
        assertFalse(settings.isEnabled(provider, disabledModel))
        assertTrue(settings.isEnabled(provider, "inherited"))
        assertEquals(
            ModelToolFallbackOverride.INHERIT,
            settings.overrideFor(provider, "inherited"),
        )
    }

    @Test
    fun explicitEnableWorksWhenGlobalDefaultIsOff() {
        val provider = "custom"
        val model = "capable-but-endpoint-rejects-tools"
        val settings = ToolCallFallbackSettings(
            enabledByDefault = false,
            enabledModelKeys = setOf(provider + "\u0000" + model),
        )

        assertTrue(settings.isEnabled(provider, model))
        assertFalse(settings.isEnabled(provider, "other"))
        assertEquals(
            ModelToolFallbackOverride.ENABLED,
            settings.overrideFor(provider, model),
        )
    }
}
