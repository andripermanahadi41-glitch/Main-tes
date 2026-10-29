package com.example.data.model

import androidx.compose.ui.graphics.Color

data class EffectModuleDef(
    val id: String,
    val colorHex: Long,
    val params: List<String>,
    val models: List<String>
) {
    val composeColor: Color get() = Color(colorHex)

    fun getParamsForModel(modelIndex: Int): List<String> {
        if (id == "NR") {
            return if (modelIndex == 0) {
                listOf("THRE")
            } else {
                listOf("THRE", "ATTACK", "REL")
            }
        }
        return params
    }

    fun getModelDescription(modelIndex: Int): String? {
        if (id == "NR") {
            return when (modelIndex) {
                0 -> "Gate 1 (Berdasarkan ISP® Decimator™): Mengadopsi teknologi pedal noise gate terkenal dengan downward expander untuk pemotongan noise yang mulus & natural. 1 kontrol utama: Threshold (Thre)."
                1 -> "Gate 2 (Modern / Flexible Gate): Gerbang noise modern fleksibel dengan 3 parameter: Threshold (Thre), Attack, dan Release (Rel) agar sustain saat bending panjang tidak terpotong."
                else -> null
            }
        }
        return null
    }
}

data class ModuleState(
    val id: String,
    var isOn: Boolean,
    var selectedModelIndex: Int,
    var values: MutableList<Float>
) {
    fun copyState(): ModuleState = ModuleState(
        id = id,
        isOn = isOn,
        selectedModelIndex = selectedModelIndex,
        values = values.toMutableList()
    )
}

data class PresetData(
    val name: String,
    val modules: List<ModuleState>,
    val rawTemplateBytes: ByteArray? = null
)

val GP180_MODULE_DEFS = listOf(
    EffectModuleDef(
        id = "NR",
        colorHex = 0xFF9AA0A6L,
        params = listOf("THRE", "ATTACK", "REL"),
        models = listOf(
            "Gate 1 (ISP Decimator)",
            "Gate 2 (Modern Gate)"
        )
    ),
    EffectModuleDef(
        id = "PRE",
        colorHex = 0xFF4FC3F7L,
        params = listOf("SUSTN/THRE", "ATTACK", "VOLUME", "TONE"),
        models = listOf(
            "S-Comp", "Keeley C4", "Dyna Comp", "Limiter", "Micro Boost",
            "Xotic AC Preamp", "Xotic RC Preamp", "Xotic BB Preamp",
            "Fortin Grind", "Acoustic Sim", "Pickup Modeler", "Touch Wah", "Step Filter"
        )
    ),
    EffectModuleDef(
        id = "WAH",
        colorHex = 0xFF66D97AL,
        params = listOf("POSITION", "RANGE", "PITCH", "MIX"),
        models = listOf("CryBaby", "Vox V847", "Petrucci Wah", "Octaver", "Detune", "Pitch Bend", "Harmonizer")
    ),
    EffectModuleDef(
        id = "DST",
        colorHex = 0xFFFFB03BL,
        params = listOf("GAIN", "TONE", "VOLUME"),
        models = listOf(
            "Green OD (TS808)", "OD 9 (TS9)", "Yellow OD (SD-1)", "Penesas (Klon)",
            "Force (OCD)", "TaiChi (Zendrive)", "Darktale (RAT)", "Chief (Guv'nor)",
            "Plustortion (Dist+)", "Lazaro (Big Muff)", "Red Haze (Fuzz Face)",
            "Black Bass (B7K)", "Bass Hammer"
        )
    ),
    EffectModuleDef(
        id = "SNAP",
        colorHex = 0xFFB388FFL,
        params = listOf("INPUT GAIN", "VOLUME", "BASS", "MIDDLE", "TREBLE"),
        models = listOf("EVH 5150 III", "Revv Generator 120", "Victory Kraken", "King of Tone", "User NAM 1")
    ),
    EffectModuleDef(
        id = "AMP",
        colorHex = 0xFFFF8A5CL,
        params = listOf("GAIN", "BASS", "MID", "TREBLE", "PRESENCE", "MASTER"),
        models = listOf(
            "Fender Tweed Deluxe", "Fender 65 Twin Reverb", "Vox AC30", "Matchless Chieftain",
            "Roland JC-120", "Marshall JCM800", "Marshall JCM900", "Mesa Dual Rectifier",
            "Mesa Mark IV", "Peavey 5150", "Friedman BE-50", "Bogner", "Soldano",
            "Diezel VH4", "Ampeg SVT (Bass)", "Fender Bassman (Bass)", "Acoustic Preamp"
        )
    ),
    EffectModuleDef(
        id = "CAB",
        colorHex = 0xFFFFD24AL,
        params = listOf("MIC POSITION", "LOW CUT", "HIGH CUT", "LEVEL"),
        models = listOf("1x10", "1x12", "2x12", "4x12 Marshall V30", "4x12 Mesa V30", "User IR 1")
    ),
    EffectModuleDef(
        id = "EQ",
        colorHex = 0xFFDFE3E8L,
        params = listOf("LOW", "MID", "HIGH", "Q", "LOW CUT", "HIGH CUT", "LEVEL"),
        models = listOf("Guitar EQ 5-band", "Guitar EQ 7-band", "Bass EQ", "Parametric EQ", "HPF / LPF")
    ),
    EffectModuleDef(
        id = "MOD",
        colorHex = 0xFF4DD0E1L,
        params = listOf("RATE", "DEPTH", "TONE", "MIX"),
        models = listOf("Analog Chorus", "Stereo Chorus", "Flanger", "Phase 90", "Tremolo", "Vibrato", "Rotary Speaker", "Uni-Vibe", "Bit Smash")
    ),
    EffectModuleDef(
        id = "DLY",
        colorHex = 0xFF80CBC4L,
        params = listOf("TIME", "FEEDBACK", "TONE", "MIX"),
        models = listOf("Digital Delay", "Analog Delay", "Tape Delay", "Ping Pong", "Reverse", "Dual Delay")
    ),
    EffectModuleDef(
        id = "RVB",
        colorHex = 0xFFC58BFFL,
        params = listOf("DECAY", "PRE-DELAY", "TONE", "MIX"),
        models = listOf("Room", "Hall", "Church", "Plate", "Spring", "Shimmer", "Cloud/Space")
    ),
    EffectModuleDef(
        id = "VOL",
        colorHex = 0xFFFF6B6BL,
        params = listOf("MIN VOLUME", "MAX VOLUME"),
        models = listOf("Linear", "Logarithmic")
    )
)

fun createDefaultPreset(): PresetData {
    val modules = GP180_MODULE_DEFS.mapIndexed { index, def ->
        val on = when (def.id) {
            "DST", "WAH", "MOD" -> false
            else -> true
        }
        val values = when (def.id) {
            "AMP" -> mutableListOf(65f, 63f, 23f, 32f, 45f, 55f)
            else -> MutableList(def.params.size) { 50f }
        }
        ModuleState(
            id = def.id,
            isOn = on,
            selectedModelIndex = 0,
            values = values
        )
    }
    return PresetData(name = "Solo Tone - GP180", modules = modules)
}
