package app.turp.chat.ui

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelPickerSheetRegressionTest {
    @Test
    fun `near full height picker keeps native gestures but stabilizes sheet insets`() {
        val source = File("src/main/java/app/turp/chat/ui/ModelPickerSheet.kt").readText()
        assertTrue(source.contains("fillMaxHeight(0.94f)"))
        assertTrue(source.contains("sheetGesturesEnabled = true"))
        assertTrue(source.contains("contentWindowInsets = { WindowInsets(0.dp) }"))
        assertFalse(source.contains("listBoundaryGuard"))
        assertFalse(source.contains(".nestedScroll("))
    }
}
