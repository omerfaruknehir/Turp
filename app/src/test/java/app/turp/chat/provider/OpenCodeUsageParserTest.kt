package app.turp.chat.provider

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OpenCodeUsageParserTest {
    @Test
    fun parsesGoUsageWindowsAndResetTimes() {
        val root = Json.parseToJsonElement(
            """
            {
              "usage": {
                "rolling": {"status":"ok","percent":12.5,"resetsAt":"2026-09-21T12:00:00Z"},
                "weekly": {"status":"rate-limited","percent":100,"resetsAt":"2026-09-25T00:00:00Z"},
                "monthly": {"status":"ok","percent":54.2,"resetsAt":null}
              }
            }
            """.trimIndent(),
        ).jsonObject

        val snapshot = OpenCodeUsageParser.parse(root, nowMs = 1234L)

        assertEquals(12.5, snapshot.rolling?.usedPercent ?: -1.0, 0.001)
        assertEquals("ok", snapshot.rolling?.status)
        assertEquals(1_789_992_000L, snapshot.rolling?.resetsAtEpochSeconds)
        assertEquals(100.0, snapshot.weekly?.usedPercent ?: -1.0, 0.001)
        assertEquals("rate-limited", snapshot.weekly?.status)
        assertEquals(54.2, snapshot.monthly?.usedPercent ?: -1.0, 0.001)
        assertNull(snapshot.monthly?.resetsAtEpochSeconds)
        assertEquals(1234L, snapshot.fetchedAtEpochMs)
    }

    @Test
    fun clampsServerPercentageIntoDisplayRange() {
        val root = Json.parseToJsonElement(
            """{"usage":{"rolling":{"status":"ok","percent":140}}}""",
        ).jsonObject
        assertEquals(100.0, OpenCodeUsageParser.parse(root).rolling?.usedPercent ?: -1.0, 0.001)
    }
}
