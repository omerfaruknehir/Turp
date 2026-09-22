package app.turp.chat.provider

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenCodeV2ModelDiscoveryTest {
    @Test
    fun `parses V2 model refs limits and per million prices without rescaling`() {
        val root = Json.parseToJsonElement(
            """
            {
              "data": [
                {
                  "id": "gpt-5.6",
                  "modelID": "gpt-5.6",
                  "providerID": "openai",
                  "family": "gpt",
                  "name": "GPT 5.6",
                  "capabilities": {"tools": true, "input": ["text", "image"], "output": ["text"]},
                  "variants": [],
                  "time": {"released": 1790000000000},
                  "cost": [{"input": 1.25, "output": 5.0, "cache": {"read": 0.125, "write": 0.5}}],
                  "status": "active",
                  "enabled": true,
                  "limit": {"context": 400000, "output": 128000},
                  "compatibility": {"reasoningField": "reasoning"}
                },
                {
                  "id": "old",
                  "modelID": "old",
                  "providerID": "vendor",
                  "name": "Old",
                  "capabilities": {"tools": false, "input": ["text"], "output": ["text"]},
                  "variants": [],
                  "time": {"released": 1},
                  "cost": [{"input": 1, "output": 1, "cache": {"read": 0, "write": 0}}],
                  "status": "deprecated",
                  "enabled": true,
                  "limit": {"context": 1000, "output": 100}
                }
              ]
            }
            """.trimIndent(),
        ).jsonObject

        val models = ModelDiscoveryService(oauth = null).parseOpenCodeV2Models(root["data"]!!.jsonArray)

        assertEquals(1, models.size)
        val model = models.single()
        assertEquals("openai/gpt-5.6", model.id)
        assertEquals("GPT 5.6", model.displayName)
        assertEquals(400_000, model.contextWindow)
        assertEquals(128_000, model.maxOutputTokens)
        assertEquals(1.25, model.inputCacheMissUsdPerMillion ?: -1.0, 0.000001)
        assertEquals(5.0, model.outputUsdPerMillion ?: -1.0, 0.000001)
        assertEquals(0.125, model.inputCacheHitUsdPerMillion ?: -1.0, 0.000001)
        assertEquals(1_790_000_000L, model.createdAtEpochSeconds)
        assertEquals("OpenCode V2", model.metadataSource)
        assertFalse(model.supportsVision == true)
        assertFalse(model.supportsTools == true)
        assertTrue(model.description.contains("reasoning-capable upstream model"))
    }
}
