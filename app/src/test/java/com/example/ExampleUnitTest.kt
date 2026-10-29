package com.example

import com.example.data.model.GP180_MODULE_DEFS
import com.example.data.model.createDefaultPreset
import com.example.data.parser.Gp180BinaryCodec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun defaultPreset_has12Modules() {
        val preset = createDefaultPreset()
        assertEquals("Solo Tone - GP180", preset.name)
        assertEquals(12, preset.modules.size)
    }

    @Test
    fun nrModule_hasGate1AndGate2WithDistinctParams() {
        val nrDef = GP180_MODULE_DEFS.first { it.id == "NR" }
        assertEquals(2, nrDef.models.size)
        assertTrue(nrDef.models[0].contains("Gate 1"))
        assertTrue(nrDef.models[1].contains("Gate 2"))

        // Gate 1 (ISP Decimator): 1 parameter (Threshold)
        val gate1Params = nrDef.getParamsForModel(0)
        assertEquals(listOf("THRE"), gate1Params)

        // Gate 2 (Modern / Flexible Gate): 3 parameters (Threshold, Attack, Release)
        val gate2Params = nrDef.getParamsForModel(1)
        assertEquals(listOf("THRE", "ATTACK", "REL"), gate2Params)

        // Verify descriptions exist and detail the technologies
        val desc1 = nrDef.getModelDescription(0)
        assertNotNull(desc1)
        assertTrue(desc1!!.contains("ISP") && desc1.contains("downward expander"))

        val desc2 = nrDef.getModelDescription(1)
        assertNotNull(desc2)
        assertTrue(desc2!!.contains("Attack") && desc2.contains("Release"))
    }

    @Test
    fun binaryCodec_buildsValidGp180Bytes() {
        val preset = createDefaultPreset()
        val result = Gp180BinaryCodec.build(preset)

        assertNotNull(result.bytes)
        assertTrue(result.bytes.size >= 0x3B4)

        // Verify roundtrip parse
        val parsed = Gp180BinaryCodec.parse(result.bytes)
        assertEquals(result.sanitizedName, parsed.name)
        assertEquals(12, parsed.modules.size)
    }
}
s