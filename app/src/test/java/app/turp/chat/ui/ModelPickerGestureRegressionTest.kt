package app.turp.chat.ui

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ModelPickerGestureRegressionTest {
    @Test
    fun `upward handle motion never dismisses model picker`() {
        assertFalse(
            shouldDismissModelPickerFromHandle(
                dragDistancePx = 0f,
                velocityYPxPerSecond = -5000f,
                distanceThresholdPx = 100f,
                velocityThresholdPxPerSecond = 1000f,
            ),
        )
    }

    @Test
    fun `downward handle distance or velocity dismisses model picker`() {
        assertTrue(
            shouldDismissModelPickerFromHandle(
                dragDistancePx = 120f,
                velocityYPxPerSecond = 0f,
                distanceThresholdPx = 100f,
                velocityThresholdPxPerSecond = 1000f,
            ),
        )
        assertTrue(
            shouldDismissModelPickerFromHandle(
                dragDistancePx = 0f,
                velocityYPxPerSecond = 1500f,
                distanceThresholdPx = 100f,
                velocityThresholdPxPerSecond = 1000f,
            ),
        )
    }

    @Test
    fun `model list no longer participates in bottom sheet dragging`() {
        val source = File("src/main/java/app/turp/chat/ui/ModelPickerSheet.kt").readText()
        assertTrue(source.contains("sheetGesturesEnabled = false"))
        assertTrue(source.contains("rememberDraggableState"))
        assertTrue(source.contains("model_picker_drag_handle"))
        assertFalse(source.contains("listBoundaryGuard"))
        assertFalse(source.contains(".nestedScroll("))
    }
}
