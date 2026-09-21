package app.turp.chat.ui

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToIndex
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import app.turp.chat.data.ModelEntity
import app.turp.chat.data.ProviderEntity
import app.turp.chat.data.ProviderKind
import app.turp.chat.ui.theme.TurpTheme
import java.util.concurrent.atomic.AtomicInteger
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ModelPickerScrollRegressionTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun downwardSwipeAwayFromTopDoesNotDismissPicker() {
        val dismissCount = AtomicInteger(0)
        val provider = ProviderEntity(
            id = "test-provider",
            displayName = "Test Provider",
            kind = ProviderKind.OPENAI_COMPATIBLE,
            baseUrl = "https://example.invalid",
        )
        val models = List(40) { index ->
            ModelEntity(
                providerId = provider.id,
                modelId = "model-$index",
                displayName = "Model $index",
                contextWindow = 128_000,
                maxOutputTokens = 8_192,
                inputCacheHitUsdPerMillion = 0.0,
                inputCacheMissUsdPerMillion = 1.0,
                outputUsdPerMillion = 2.0,
                pricingConfigured = true,
            )
        }

        composeRule.setContent {
            TurpTheme {
                ModelPickerSheet(
                    providers = listOf(provider),
                    models = models,
                    selectedProviderId = provider.id,
                    selectedModelId = models.first().modelId,
                    favoriteKeys = emptySet(),
                    recentKeys = emptyList(),
                    onToggleFavorite = { _, _ -> },
                    onSelect = { _, _ -> },
                    onDismiss = { dismissCount.incrementAndGet() },
                )
            }
        }

        val list = composeRule.onNodeWithTag("model_picker_list")
        list.performScrollToIndex(20)
        composeRule.waitForIdle()
        list.performTouchInput {
            swipe(
                start = center,
                end = Offset(center.x, center.y + 180f),
                durationMillis = 300,
            )
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("model_picker_sheet").assertExists()
        composeRule.runOnIdle { assertEquals(0, dismissCount.get()) }
    }

    @Test
    fun downwardSwipeAtTopDismissesPicker() {
        val dismissCount = AtomicInteger(0)
        val provider = ProviderEntity(
            id = "test-provider",
            displayName = "Test Provider",
            kind = ProviderKind.OPENAI_COMPATIBLE,
            baseUrl = "https://example.invalid",
        )
        val models = List(40) { index ->
            ModelEntity(
                providerId = provider.id,
                modelId = "model-$index",
                displayName = "Model $index",
                contextWindow = 128_000,
                maxOutputTokens = 8_192,
                inputCacheHitUsdPerMillion = 0.0,
                inputCacheMissUsdPerMillion = 1.0,
                outputUsdPerMillion = 2.0,
                pricingConfigured = true,
            )
        }

        composeRule.setContent {
            TurpTheme {
                ModelPickerSheet(
                    providers = listOf(provider),
                    models = models,
                    selectedProviderId = provider.id,
                    selectedModelId = models.first().modelId,
                    favoriteKeys = emptySet(),
                    recentKeys = emptyList(),
                    onToggleFavorite = { _, _ -> },
                    onSelect = { _, _ -> },
                    onDismiss = { dismissCount.incrementAndGet() },
                )
            }
        }

        composeRule.onNodeWithTag("model_picker_list").performTouchInput {
            swipeDown(durationMillis = 500)
        }

        composeRule.waitUntil(timeoutMillis = 3_000) { dismissCount.get() > 0 }
        composeRule.runOnIdle { assertEquals(1, dismissCount.get()) }
    }
    @Test
    fun upwardPullAtBottomSettlesWithoutDismissingPicker() {
        val dismissCount = AtomicInteger(0)
        val provider = ProviderEntity(
            id = "test-provider",
            displayName = "Test Provider",
            kind = ProviderKind.OPENAI_COMPATIBLE,
            baseUrl = "https://example.invalid",
        )
        val models = List(40) { index ->
            ModelEntity(
                providerId = provider.id,
                modelId = "model-$index",
                displayName = "Model $index",
                contextWindow = 128_000,
                maxOutputTokens = 8_192,
                inputCacheHitUsdPerMillion = 0.0,
                inputCacheMissUsdPerMillion = 1.0,
                outputUsdPerMillion = 2.0,
                pricingConfigured = true,
            )
        }

        composeRule.setContent {
            TurpTheme {
                ModelPickerSheet(
                    providers = listOf(provider),
                    models = models,
                    selectedProviderId = provider.id,
                    selectedModelId = models.first().modelId,
                    favoriteKeys = emptySet(),
                    recentKeys = emptyList(),
                    onToggleFavorite = { _, _ -> },
                    onSelect = { _, _ -> },
                    onDismiss = { dismissCount.incrementAndGet() },
                )
            }
        }

        val list = composeRule.onNodeWithTag("model_picker_list")
        list.performScrollToIndex(models.lastIndex)
        composeRule.waitForIdle()
        list.performTouchInput { swipeUp(durationMillis = 500) }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("model_picker_sheet").assertExists()
        composeRule.runOnIdle { assertEquals(0, dismissCount.get()) }
    }

    @Test
    fun downwardSwipeOnDragHandleDismissesPicker() {
        val dismissCount = AtomicInteger(0)
        val provider = ProviderEntity(
            id = "test-provider",
            displayName = "Test Provider",
            kind = ProviderKind.OPENAI_COMPATIBLE,
            baseUrl = "https://example.invalid",
        )
        val models = List(40) { index ->
            ModelEntity(
                providerId = provider.id,
                modelId = "model-$index",
                displayName = "Model $index",
                contextWindow = 128_000,
                maxOutputTokens = 8_192,
                inputCacheHitUsdPerMillion = 0.0,
                inputCacheMissUsdPerMillion = 1.0,
                outputUsdPerMillion = 2.0,
                pricingConfigured = true,
            )
        }

        composeRule.setContent {
            TurpTheme {
                ModelPickerSheet(
                    providers = listOf(provider),
                    models = models,
                    selectedProviderId = provider.id,
                    selectedModelId = models.first().modelId,
                    favoriteKeys = emptySet(),
                    recentKeys = emptyList(),
                    onToggleFavorite = { _, _ -> },
                    onSelect = { _, _ -> },
                    onDismiss = { dismissCount.incrementAndGet() },
                )
            }
        }

        composeRule.onNodeWithTag("model_picker_drag_handle", useUnmergedTree = true).assertExists()
        composeRule.onNodeWithTag("model_picker_drag_handle", useUnmergedTree = true).performTouchInput {
            swipe(
                start = center,
                end = Offset(center.x, center.y + 600f),
                durationMillis = 450,
            )
        }

        composeRule.waitUntil(timeoutMillis = 3_000) { dismissCount.get() > 0 }
        composeRule.runOnIdle { assertEquals(1, dismissCount.get()) }
    }

}
