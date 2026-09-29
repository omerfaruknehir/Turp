package app.turp.chat.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ChatScrollMathTest {
    @Test
    fun autoFollowStepIsPositiveBoundedAndMonotonic() {
        val small = calculateAutoFollowStepPx(8f, 1f / 60f, 48_000f)
        val medium = calculateAutoFollowStepPx(80f, 1f / 60f, 48_000f)
        val large = calculateAutoFollowStepPx(800f, 1f / 60f, 48_000f)
        assertTrue(small > 0f)
        assertTrue(medium > small)
        assertTrue(large >= medium)
        assertTrue(small <= 8f)
        assertTrue(medium <= 80f)
        assertTrue(large <= 800f + .001f) // speed cap: 48000 / 60
        assertTrue(large > medium * 6f) // distance response is intentionally non-linear
    }

    @Test
    fun autoFollowStepHandlesInvalidInputs() {
        assertEquals(0f, calculateAutoFollowStepPx(0f, 1f / 60f, 4_800f), 0f)
        assertEquals(0f, calculateAutoFollowStepPx(10f, 0f, 4_800f), 0f)
        assertEquals(0f, calculateAutoFollowStepPx(10f, 1f / 60f, 0f), 0f)
    }


    @Test
    fun offscreenSeekAcceleratesNonLinearlyWithDistanceAndTime() {
        val nearInitial = calculateAutoFollowSeekSpeedPxPerSecond(
            hiddenItemCount = 1,
            elapsedSeconds = 0f,
            minSpeedPxPerSecond = 6_000f,
            maxSpeedPxPerSecond = 72_000f,
        )
        val farInitial = calculateAutoFollowSeekSpeedPxPerSecond(
            hiddenItemCount = 8,
            elapsedSeconds = 0f,
            minSpeedPxPerSecond = 6_000f,
            maxSpeedPxPerSecond = 72_000f,
        )
        val nearAfterCatchUp = calculateAutoFollowSeekSpeedPxPerSecond(
            hiddenItemCount = 1,
            elapsedSeconds = 0.25f,
            minSpeedPxPerSecond = 6_000f,
            maxSpeedPxPerSecond = 72_000f,
        )

        assertTrue(nearInitial > 6_000f)
        assertTrue(farInitial > nearInitial)
        assertTrue(nearAfterCatchUp > nearInitial)
        assertTrue(farInitial <= 72_000f)
        assertTrue(nearAfterCatchUp <= 72_000f)
    }

    @Test
    fun offscreenSeekRejectsInvalidInputs() {
        assertEquals(0f, calculateAutoFollowSeekSpeedPxPerSecond(0, 0f, 6_000f, 72_000f), 0f)
        assertEquals(0f, calculateAutoFollowSeekSpeedPxPerSecond(1, -1f, 6_000f, 72_000f), 0f)
        assertEquals(0f, calculateAutoFollowSeekSpeedPxPerSecond(1, 0f, 72_000f, 6_000f), 0f)
    }

    @Test
    fun viewportCorrectionUsesDriftDirection() {
        assertEquals(24f, calculateViewportCorrectionDeltaPx(124, 100), 0f)
        assertEquals(-18f, calculateViewportCorrectionDeltaPx(82, 100), 0f)
        assertEquals(0f, calculateViewportCorrectionDeltaPx(100, 100), 0f)
    }
    @Test
    fun cardPinningAndCenteringUseTheSameScrollDirection() {
        assertEquals(18f, calculateCardViewportCorrectionPx(118f, 100f), 0f)
        assertEquals(-12f, calculateCardViewportCorrectionPx(88f, 100f), 0f)
        assertEquals(50f, calculateCenteredCardCorrectionPx(450f, 550f, 100f, 800f), 0f)
    }

    @Test
    fun onlyLargeExpandedCardsAreCenteredAfterCollapse() {
        assertTrue(shouldCenterCollapsedCard(550f, 900f))
        assertTrue(!shouldCenterCollapsedCard(400f, 900f))
    }


    @Test
    fun workingCardViewportAnchorMatchesInteractionAndCardPosition() {
        assertEquals(
            WorkingCardViewportAnchor.TOP,
            chooseWorkingCardViewportAnchor(
                manual = true,
                followingLatest = true,
                cardTopPx = 900f,
                cardBottomPx = 1_200f,
                viewportTopPx = 100f,
                viewportBottomPx = 800f,
            ),
        )
        assertEquals(
            WorkingCardViewportAnchor.LATEST,
            chooseWorkingCardViewportAnchor(
                manual = false,
                followingLatest = true,
                cardTopPx = 200f,
                cardBottomPx = 600f,
                viewportTopPx = 100f,
                viewportBottomPx = 800f,
            ),
        )
        assertEquals(
            WorkingCardViewportAnchor.BOTTOM,
            chooseWorkingCardViewportAnchor(
                manual = false,
                followingLatest = false,
                cardTopPx = -300f,
                cardBottomPx = 80f,
                viewportTopPx = 100f,
                viewportBottomPx = 800f,
            ),
        )
        assertEquals(
            WorkingCardViewportAnchor.BOTTOM,
            chooseWorkingCardViewportAnchor(
                manual = false,
                followingLatest = false,
                cardTopPx = 50f,
                cardBottomPx = 350f,
                viewportTopPx = 100f,
                viewportBottomPx = 800f,
            ),
        )
        assertEquals(
            WorkingCardViewportAnchor.TOP,
            chooseWorkingCardViewportAnchor(
                manual = false,
                followingLatest = false,
                cardTopPx = 250f,
                cardBottomPx = 600f,
                viewportTopPx = 100f,
                viewportBottomPx = 800f,
            ),
        )
        assertEquals(
            WorkingCardViewportAnchor.NONE,
            chooseWorkingCardViewportAnchor(
                manual = false,
                followingLatest = false,
                cardTopPx = 850f,
                cardBottomPx = 1_100f,
                viewportTopPx = 100f,
                viewportBottomPx = 800f,
            ),
        )
    }

    @Test
    fun messageBoundaryScrollAvoidsCompositionChurn() {
        val chat = java.io.File("src/main/java/app/turp/chat/ui/ChatScreen.kt").readText()
        val viewModel = java.io.File("src/main/java/app/turp/chat/ui/ChatViewModel.kt").readText()
        val messageRendering = chat
            .substringAfter("private fun MessageCard(")
            .substringBefore("private fun Composer(")

        assertFalse(chat.contains("cardBounds by remember"))
        assertFalse(chat.contains("cardBounds = it.boundsInRoot()"))
        assertFalse(messageRendering.contains("developerSettings.collectAsStateWithLifecycle()"))
        assertFalse(messageRendering.contains("toolCallFallbackSettings.collectAsStateWithLifecycle()"))
        assertTrue(messageRendering.contains("MessageTimelineDecodeCache.decode"))
        assertTrue(messageRendering.contains("ToolTraceDecodeCache.decode"))
        assertTrue(messageRendering.contains("remember(message.nodeId) { viewModel.observeAttachments(message.nodeId) }"))
        assertTrue(messageRendering.contains("initialValue = viewModel.attachmentSnapshot(message.nodeId)"))
        assertTrue(chat.contains("viewModel.prewarmAttachments(message.nodeId)"))
        assertTrue(viewModel.contains("attachmentSnapshotCache"))
        assertTrue(viewModel.contains("attachmentFlowCache"))
        assertTrue(chat.contains("if (cardCoordinates !== coordinates) cardCoordinates = coordinates"))
        assertTrue(chat.contains("MESSAGE_RENDER_AHEAD_COUNT = 5"))
        assertTrue(chat.contains("MESSAGE_RENDER_BEHIND_COUNT = 2"))
        assertTrue(chat.contains("ChatFollowSeekMinSpeedPxPerSecond = 9_000f"))
        assertTrue(chat.contains("ChatFollowSeekMaxSpeedPxPerSecond = 36_000f"))
        assertTrue(chat.contains("ChatFollowSeekMaxFrameStepPx = 320f"))
        assertTrue(chat.contains("prewarmRichMessageRendering("))
        assertTrue(chat.contains("ChatMessageCacheWindow"))
        assertTrue(chat.contains("CHAT_CACHE_AHEAD_VIEWPORTS = 3f"))
        assertTrue(chat.contains("CHAT_CACHE_BEHIND_VIEWPORTS = 2f"))
        assertTrue(chat.contains("rememberLazyListState(cacheWindow = ChatMessageCacheWindow)"))
        assertFalse(chat.contains("private class ChatMessagePrefetchStrategy"))
        assertTrue(chat.contains("chatDataPrewarmIndices("))
        assertTrue(chat.contains(".conflate()"))
        assertTrue(chat.contains(".collect { candidates ->"))
        assertFalse(
            chat.substringBefore("private fun MessageCard(")
                .contains("developerHttpTraces by viewModel.developerHttpTraces.collectAsStateWithLifecycle()"),
        )
        assertTrue(
            messageRendering.contains(
                "developerHttpTraces by viewModel.developerHttpTraces.collectAsStateWithLifecycle()",
            ),
        )
    }

    @Test
    fun retainedCacheWindowCoversMultipleViewports() {
        assertEquals(3_000, chatCacheWindowPx(viewportPx = 1_000, viewportMultiplier = 3f))
        assertEquals(2_000, chatCacheWindowPx(viewportPx = 1_000, viewportMultiplier = 2f))
        assertEquals(1_000, chatCacheWindowPx(viewportPx = 1_000, viewportMultiplier = 0.5f))
        assertEquals(0, chatCacheWindowPx(viewportPx = 0, viewportMultiplier = 3f))
        assertEquals(0, chatCacheWindowPx(viewportPx = 1_000, viewportMultiplier = 0f))
    }

    @Test
    fun messageBoundaryPrefetchTargetsOnlyOffscreenNeighbors() {
        assertEquals(
            listOf(6, 1, 7, 0, 8, 9),
            chatComposePrefetchIndices(
                firstVisibleIndex = 2,
                lastVisibleIndex = 5,
                itemCount = 10,
                aheadCount = 4,
                behindCount = 2,
            ),
        )
        assertEquals(
            listOf(3, 4),
            chatComposePrefetchIndices(
                firstVisibleIndex = 0,
                lastVisibleIndex = 2,
                itemCount = 5,
                aheadCount = 4,
                behindCount = 2,
            ),
        )
        assertTrue(
            chatComposePrefetchIndices(
                firstVisibleIndex = 3,
                lastVisibleIndex = 2,
                itemCount = 10,
            ).isEmpty(),
        )
    }

    @Test
    fun gesturePrefetchRunsBeforeTheVisibleSetChanges() {
        assertEquals(
            listOf(8, 9, 10, 11, 12, 13),
            chatGesturePrefetchIndices(
                delta = -24f,
                firstVisibleIndex = 4,
                lastVisibleIndex = 7,
                itemCount = 20,
            ),
        )
        assertEquals(
            listOf(3, 2, 1, 0),
            chatGesturePrefetchIndices(
                delta = 24f,
                firstVisibleIndex = 4,
                lastVisibleIndex = 7,
                itemCount = 20,
            ),
        )
        assertTrue(
            chatGesturePrefetchIndices(
                delta = 0f,
                firstVisibleIndex = 4,
                lastVisibleIndex = 7,
                itemCount = 20,
            ).isEmpty(),
        )
    }

    @Test
    fun prefetchRetentionKeepsRowsAsTheyEnterViewport() {
        val retained = chatPrefetchRetentionIndices(
            firstVisibleIndex = 10,
            lastVisibleIndex = 14,
            itemCount = 40,
            aheadCount = 10,
            behindCount = 8,
        )
        assertTrue(14 in retained)
        assertTrue(15 in retained)
        assertTrue(24 in retained)
        assertTrue(2 in retained)
        assertFalse(1 in retained)
        assertFalse(25 in retained)

        val afterBoundaryCross = chatPrefetchRetentionIndices(
            firstVisibleIndex = 11,
            lastVisibleIndex = 15,
            itemCount = 40,
            aheadCount = 10,
            behindCount = 8,
        )
        assertTrue(15 in afterBoundaryCross)
        assertTrue(14 in afterBoundaryCross)
    }

    @Test
    fun dataPrewarmDoesNotRepeatVisibleRows() {
        val indices = chatDataPrewarmIndices(
            firstVisibleIndex = 4,
            lastVisibleIndex = 7,
            itemCount = 20,
            aheadCount = 5,
            behindCount = 2,
        )
        assertTrue(indices.none { it in 4..7 })
        assertEquals(listOf(8, 3, 9, 2, 10, 11, 12), indices)
    }

    @Test
    fun markdownAndroidViewsPreserveLayoutWhilePooled() {
        val rich = java.io.File("src/main/java/app/turp/chat/ui/RichMessage.kt").readText()
        assertTrue(rich.contains("onReset = { it.prepareForReuse() }"))
        assertTrue(rich.contains("onRelease = { it.resetForRelease() }"))
        assertTrue(rich.contains("fun prepareForReuse()"))
        assertTrue(rich.contains("fun resetForRelease()"))
        assertFalse(
            rich.substringAfter("fun prepareForReuse()")
                .substringBefore("fun resetForRelease()")
                .contains("text = null"),
        )
        assertTrue(
            rich.substringAfter("fun resetForRelease()")
                .substringBefore("private class SelectableLinkMovementMethod")
                .contains("text = null"),
        )
    }

    @Test
    fun descendingPagingIsMappedToChronologicalUiOrder() {
        assertEquals(4, chronologicalSourceIndex(0, 5))
        assertEquals(0, chronologicalSourceIndex(4, 5))
        assertEquals(0, chronologicalUiIndex(4, 5))
        assertEquals(4, chronologicalUiIndex(0, 5))
    }

    @Test
    fun visibleViewportEndsAboveComposerAndBottomGutter() {
        assertEquals(700, calculateVisibleChatViewportEndPx(viewportEndPx = 1_000, obscuredBottomPx = 300))
        assertEquals(0, calculateVisibleChatViewportEndPx(viewportEndPx = 200, obscuredBottomPx = 300))
        assertEquals(1_000, calculateVisibleChatViewportEndPx(viewportEndPx = 1_000, obscuredBottomPx = -10))
    }

    @Test
    fun streamingAnchorRestoresOnlyUnexpectedUpwardRegression() {
        assertTrue(shouldRestoreStreamingAnchor(previousItemIndex = 40, currentItemIndex = 0, userDragging = false))
        assertTrue(shouldRestoreStreamingAnchor(previousItemIndex = 40, currentItemIndex = 38, userDragging = false))
        assertTrue(!shouldRestoreStreamingAnchor(previousItemIndex = 40, currentItemIndex = 39, userDragging = false))
        assertTrue(!shouldRestoreStreamingAnchor(previousItemIndex = 40, currentItemIndex = 0, userDragging = true))
        assertTrue(!shouldRestoreStreamingAnchor(previousItemIndex = 40, currentItemIndex = 45, userDragging = false))
    }

}
