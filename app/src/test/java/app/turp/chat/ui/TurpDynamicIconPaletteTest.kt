package app.turp.chat.ui

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class TurpDynamicIconPaletteTest {
    @Test
    fun `Turp launcher geometry keeps every static palette distinct`() {
        val expected = mapOf(
            "graphite" to listOf("#FFA9C7F8", "#FFE5BFA6"),
            "ocean" to listOf("#FF54D6F2", "#FFBEC6EA"),
            "violet" to listOf("#FFD1BCFF", "#FFEFB8C8"),
            "sunset" to listOf("#FFFFB59C", "#FFD7C58D"),
        )
        expected.forEach { (name, colors) ->
            val source = File("src/main/res/drawable/ic_turp_foreground_$name.xml").readText()
            assertTrue(name, source.contains("M45.355735,12.06325"))\n            assertTrue(name, source.contains("M54,24.484818"))
            colors.forEach { color -> assertTrue("$name missing $color", source.contains(color)) }
        }
    }
}
