package app.turp.chat.ui

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class TurpDynamicIconPaletteTest {
    @Test
    fun `Turp launcher keeps both glyph layers in each palette hue family`() {
        val expected = mapOf(
            "arbor" to listOf("#FF99D5B1", "#FFB5F1CC"),
            "graphite" to listOf("#FFA9C7F8", "#FFB4CDF5"),
            "ocean" to listOf("#FF54D6F2", "#FF8AE4F6"),
            "violet" to listOf("#FFD1BCFF", "#FFD6C2FF"),
            "sunset" to listOf("#FFFFB59C", "#FFE67853"),
        )
        expected.forEach { (name, colors) ->
            val source = File("src/main/res/drawable/ic_turp_foreground_$name.xml").readText()
            assertTrue(name, source.contains("M45.355735,12.06325"))
            assertTrue(name, source.contains("M54,24.484818"))
            colors.forEach { color -> assertTrue("$name missing $color", source.contains(color)) }
        }
    }
}
