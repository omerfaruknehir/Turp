package app.turp.chat.provider

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenRouterKeyParserTest {
    @Test
    fun `parses current key limits usage BYOK regions expiry and free allowance`() {
        val root = Json.parseToJsonElement(
            """
            {
              "data": {
                "label": "Turp",
                "limit": 50.0,
                "limit_remaining": 37.5,
                "limit_reset": "monthly",
                "usage": 12.5,
                "usage_daily": 1.25,
                "usage_weekly": 4.5,
                "usage_monthly": 10.0,
                "byok_usage": 2.0,
                "byok_usage_daily": 0.2,
                "byok_usage_weekly": 0.8,
                "byok_usage_monthly": 1.6,
                "include_byok_in_limit": true,
                "is_free_tier": false,
                "allowed_data_regions": ["europe", "us"],
                "expires_at": "2026-10-01T00:00:00Z",
                "free_model_daily_requests": {"limit": 50, "remaining": 42, "used": 8}
              }
            }
            """.trimIndent(),
        ).jsonObject

        val value = OpenRouterKeyParser.parse(root, nowMs = 123L)
        assertEquals("Turp", value.label)
        assertEquals(50.0, value.limitUsd ?: -1.0, 0.000001)
        assertEquals(37.5, value.limitRemainingUsd ?: -1.0, 0.000001)
        assertEquals("monthly", value.limitReset)
        assertEquals(12.5, value.usageUsd ?: -1.0, 0.000001)
        assertEquals(1.25, value.usageDailyUsd ?: -1.0, 0.000001)
        assertEquals(2.0, value.byokUsageUsd ?: -1.0, 0.000001)
        assertTrue(value.includeByokInLimit == true)
        assertFalse(value.isFreeTier == true)
        assertEquals(listOf("europe", "us"), value.allowedDataRegions)
        assertEquals(1_801_440_000L, value.expiresAtEpochSeconds)
        assertEquals(50, value.freeModelDailyRequests?.limit)
        assertEquals(42, value.freeModelDailyRequests?.remaining)
        assertEquals(8, value.freeModelDailyRequests?.used)
        assertEquals(123L, value.fetchedAtEpochMs)
    }
}
