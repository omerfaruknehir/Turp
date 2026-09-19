package app.turp.chat.demo

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.turp.chat.TurpApplication
import app.turp.chat.data.MessageStatus
import app.turp.chat.data.SendMode
import app.turp.chat.settings.NewChatDefaults
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DemoModeIntegrationTest {
    @Test
    fun demoModeSeedsRespondsAndCleansUpNamespacedFixtures() = runBlocking {
        val application = ApplicationProvider.getApplicationContext<TurpApplication>()
        val container = application.container

        container.demoMode.setEnabled(false)
        try {
            container.demoMode.setEnabled(true)

            val demoProviders = container.database.catalogDao().allProviders()
                .filter { DemoModeController.isDemoProviderId(it.id) }
            val demoModels = container.database.catalogDao().allModels()
                .filter { DemoModeController.isDemoProviderId(it.providerId) }
            val demoProjects = container.database.projectDao().all()
                .filter { it.id.startsWith(DemoModeController.DEMO_PREFIX) }
            val demoChats = container.database.conversationDao().all()
                .filter { DemoModeController.isDemoConversationId(it.id) }

            assertEquals(5, demoProviders.size)
            assertEquals(100, demoModels.size)
            assertEquals(3, demoProjects.size)
            assertEquals(12, demoChats.size)
            assertTrue(container.appPreferences.developerSettings.value.demoModeEnabled)

            val effectiveDefaults = container.demoMode.effectiveNewChatDefaults(NewChatDefaults())
            assertEquals(DemoModeController.DEFAULT_PROVIDER_ID, effectiveDefaults.selectedProviderId)
            assertEquals(DemoModeController.DEFAULT_MODEL_ID, effectiveDefaults.selectedModelId)

            val walkthrough = container.repository.conversationNow(DemoModeController.WALKTHROUGH_CHAT_ID)
            assertNotNull(walkthrough)
            assertTrue(container.demoMode.handlesConversation(DemoModeController.WALKTHROUGH_CHAT_ID))

            val assistantId = container.demoMode.submit(
                DemoModeController.WALKTHROUGH_CHAT_ID,
                "Give me a deterministic demo reply.",
                emptyList(),
                SendMode.SEND_NOW,
            )
            assertNotNull(assistantId)
            container.demoMode.completeResponse(
                DemoModeController.WALKTHROUGH_CHAT_ID,
                requireNotNull(assistantId),
            )

            val completed = container.repository.message(assistantId)
            assertNotNull(completed)
            assertEquals(MessageStatus.COMPLETE, completed?.status)
            assertTrue(completed?.content.orEmpty().isNotBlank())
            assertTrue((completed?.outputTokens ?: 0L) > 0L)
        } finally {
            container.demoMode.setEnabled(false)
        }

        assertFalse(container.appPreferences.developerSettings.value.demoModeEnabled)
        assertTrue(
            container.database.catalogDao().allProviders()
                .none { DemoModeController.isDemoProviderId(it.id) },
        )
        assertTrue(
            container.database.catalogDao().allModels()
                .none { DemoModeController.isDemoProviderId(it.providerId) },
        )
        assertTrue(
            container.database.projectDao().all()
                .none { it.id.startsWith(DemoModeController.DEMO_PREFIX) },
        )
        assertTrue(
            container.database.conversationDao().all()
                .none { DemoModeController.isDemoConversationId(it.id) },
        )
    }
}
