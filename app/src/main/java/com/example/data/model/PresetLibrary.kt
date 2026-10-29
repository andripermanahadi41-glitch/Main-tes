package com.example.data.model

data class LibraryPresetItem(
    val title: String,
    val category: String, // "Rock / Metal", "Blues / Crunch", "Clean / Ambient", "Bass Grooves"
    val description: String,
    val instrument: String, // "Gitar" or "Bass"
    val preset: PresetData
)

object PresetLibrary {
    val BUILTIN_PRESETS = listOf(
        LibraryPresetItem(
            title = "Solo Tone - GP180",
            category = "Rock / Metal",
            description = "High-gain singing lead with JCM800, delay & hall reverb",
            instrument = "Gitar",
            preset = createDefaultPreset()
        ),
        LibraryPresetItem(
            title = "Master of Puppets",
            category = "Rock / Metal",
            description = "Iconic 80s thrash rhythm & lead with Peavey 5150 & TS9 boost",
            instrument = "Gitar",
            preset = PresetData(
                name = "Puppets Thrash",
                modules = GP180_MODULE_DEFS.map { def ->
                    when (def.id) {
                        "NR" -> ModuleState("NR", isOn = true, 0, mutableListOf(70f, 30f, 40f))
                        "PRE" -> ModuleState("PRE", isOn = false, 0, mutableListOf(50f, 50f, 50f, 50f))
                        "WAH" -> ModuleState("WAH", isOn = false, 0, mutableListOf(50f, 50f, 50f, 50f))
                        "DST" -> ModuleState("DST", isOn = true, 1 /* TS9 */, mutableListOf(25f, 70f, 80f))
                        "SNAP" -> ModuleState("SNAP", isOn = false, 0, mutableListOf(50f, 50f, 50f, 50f, 50f))
                        "AMP" -> ModuleState("AMP", isOn = true, 9 /* Peavey 5150 */, mutableListOf(78f, 70f, 30f, 68f, 60f, 65f))
                        "CAB" -> ModuleState("CAB", isOn = true, 4 /* 4x12 Mesa */, mutableListOf(45f, 40f, 80f, 70f))
                        "EQ" -> ModuleState("EQ", isOn = true, 0, mutableListOf(65f, 35f, 70f, 50f, 40f, 85f, 60f))
                        "MOD" -> ModuleState("MOD", isOn = false, 0, mutableListOf(40f, 40f, 50f, 30f))
                        "DLY" -> ModuleState("DLY", isOn = false, 0, mutableListOf(30f, 25f, 50f, 20f))
                        "RVB" -> ModuleState("RVB", isOn = true, 0 /* Room */, mutableListOf(30f, 15f, 40f, 25f))
                        "VOL" -> ModuleState("VOL", isOn = true, 0, mutableListOf(0f, 100f))
                        else -> ModuleState(def.id, true, 0, MutableList(def.params.size) { 50f })
                    }
                }
            )
        ),
        LibraryPresetItem(
            title = "Comfortably Numb",
            category = "Rock / Metal",
            description = "Gilmour emotional soaring solo tone with Big Muff & tape delay",
            instrument = "Gitar",
            preset = PresetData(
                name = "Comfort Numb",
                modules = GP180_MODULE_DEFS.map { def ->
                    when (def.id) {
                        "NR" -> ModuleState("NR", isOn = true, 0, mutableListOf(55f, 40f, 50f))
                        "PRE" -> ModuleState("PRE", isOn = true, 1 /* Keeley C4 */, mutableListOf(60f, 50f, 65f, 55f))
                        "WAH" -> ModuleState("WAH", isOn = false, 0, mutableListOf(50f, 50f, 50f, 50f))
                        "DST" -> ModuleState("DST", isOn = true, 9 /* Big Muff */, mutableListOf(65f, 52f, 70f))
                        "SNAP" -> ModuleState("SNAP", isOn = false, 0, mutableListOf(50f, 50f, 50f, 50f, 50f))
                        "AMP" -> ModuleState("AMP", isOn = true, 7 /* Mesa Dual Rectifier */, mutableListOf(55f, 60f, 60f, 58f, 52f, 70f))
                        "CAB" -> ModuleState("CAB", isOn = true, 3 /* 4x12 Marshall */, mutableListOf(50f, 35f, 75f, 65f))
                        "EQ" -> ModuleState("EQ", isOn = true, 1 /* 7-band */, mutableListOf(55f, 60f, 60f, 50f, 35f, 80f, 60f))
                        "MOD" -> ModuleState("MOD", isOn = true, 0 /* Chorus */, mutableListOf(30f, 45f, 55f, 28f))
                        "DLY" -> ModuleState("DLY", isOn = true, 2 /* Tape Delay */, mutableListOf(52f, 48f, 45f, 40f))
                        "RVB" -> ModuleState("RVB", isOn = true, 1 /* Hall */, mutableListOf(60f, 35f, 45f, 35f))
                        "VOL" -> ModuleState("VOL", isOn = true, 0, mutableListOf(0f, 100f))
                        else -> ModuleState(def.id, true, 0, MutableList(def.params.size) { 50f })
                    }
                }
            )
        ),
        LibraryPresetItem(
            title = "Texas Blues Pride",
            category = "Blues / Crunch",
            description = "SRV strat blues bite with cranked TS808 into Fender Twin",
            instrument = "Gitar",
            preset = PresetData(
                name = "Texas Blues",
                modules = GP180_MODULE_DEFS.map { def ->
                    when (def.id) {
                        "NR" -> ModuleState("NR", isOn = false, 0, mutableListOf(40f, 50f, 50f))
                        "PRE" -> ModuleState("PRE", isOn = true, 2 /* Dyna Comp */, mutableListOf(45f, 45f, 70f, 50f))
                        "WAH" -> ModuleState("WAH", isOn = false, 0, mutableListOf(50f, 50f, 50f, 50f))
                        "DST" -> ModuleState("DST", isOn = true, 0 /* TS808 */, mutableListOf(35f, 65f, 85f))
                        "SNAP" -> ModuleState("SNAP", isOn = false, 0, mutableListOf(50f, 50f, 50f, 50f, 50f))
                        "AMP" -> ModuleState("AMP", isOn = true, 0 /* Tweed Deluxe */, mutableListOf(62f, 55f, 65f, 60f, 55f, 70f))
                        "CAB" -> ModuleState("CAB", isOn = true, 1 /* 1x12 */, mutableListOf(50f, 30f, 80f, 70f))
                        "EQ" -> ModuleState("EQ", isOn = false, 0, mutableListOf(50f, 50f, 50f, 50f, 30f, 80f, 50f))
                        "MOD" -> ModuleState("MOD", isOn = false, 0, mutableListOf(30f, 40f, 50f, 25f))
                        "DLY" -> ModuleState("DLY", isOn = false, 1 /* Analog Delay */, mutableListOf(25f, 20f, 40f, 20f))
                        "RVB" -> ModuleState("RVB", isOn = true, 4 /* Spring */, mutableListOf(45f, 20f, 55f, 30f))
                        "VOL" -> ModuleState("VOL", isOn = true, 0, mutableListOf(0f, 100f))
                        else -> ModuleState(def.id, true, 0, MutableList(def.params.size) { 50f })
                    }
                }
            )
        ),
        LibraryPresetItem(
            title = "Clean Sparkle & Chorus",
            category = "Clean / Ambient",
            description = "Lush stereo chorus, glassy clean Fender 65 & shimmer reverb",
            instrument = "Gitar",
            preset = PresetData(
                name = "Clean Sparkle",
                modules = GP180_MODULE_DEFS.map { def ->
                    when (def.id) {
                        "NR" -> ModuleState("NR", isOn = false, 0, mutableListOf(30f, 50f, 50f))
                        "PRE" -> ModuleState("PRE", isOn = true, 0 /* S-Comp */, mutableListOf(50f, 40f, 65f, 60f))
                        "WAH" -> ModuleState("WAH", isOn = false, 0, mutableListOf(50f, 50f, 50f, 50f))
                        "DST" -> ModuleState("DST", isOn = false, 3 /* Klon */, mutableListOf(20f, 50f, 50f))
                        "SNAP" -> ModuleState("SNAP", isOn = false, 0, mutableListOf(50f, 50f, 50f, 50f, 50f))
                        "AMP" -> ModuleState("AMP", isOn = true, 1 /* Fender 65 Twin */, mutableListOf(35f, 60f, 45f, 70f, 65f, 75f))
                        "CAB" -> ModuleState("CAB", isOn = true, 2 /* 2x12 */, mutableListOf(55f, 35f, 85f, 65f))
                        "EQ" -> ModuleState("EQ", isOn = false, 0, mutableListOf(50f, 50f, 50f, 50f, 30f, 80f, 50f))
                        "MOD" -> ModuleState("MOD", isOn = true, 1 /* Stereo Chorus */, mutableListOf(42f, 60f, 55f, 45f))
                        "DLY" -> ModuleState("DLY", isOn = true, 0 /* Digital Delay */, mutableListOf(40f, 35f, 50f, 30f))
                        "RVB" -> ModuleState("RVB", isOn = true, 5 /* Shimmer */, mutableListOf(65f, 30f, 60f, 40f))
                        "VOL" -> ModuleState("VOL", isOn = true, 0, mutableListOf(0f, 100f))
                        else -> ModuleState(def.id, true, 0, MutableList(def.params.size) { 50f })
                    }
                }
            )
        ),
        LibraryPresetItem(
            title = "Another One Bites the Dust",
            category = "Bass Grooves",
            description = "Tight, punchy funk bass tone with Ampeg SVT & fast compressor",
            instrument = "Bass",
            preset = PresetData(
                name = "Bites The Dust",
                modules = GP180_MODULE_DEFS.map { def ->
                    when (def.id) {
                        "NR" -> ModuleState("NR", isOn = true, 0, mutableListOf(45f, 35f, 50f))
                        "PRE" -> ModuleState("PRE", isOn = true, 3 /* Limiter */, mutableListOf(65f, 30f, 70f, 60f))
                        "WAH" -> ModuleState("WAH", isOn = false, 0, mutableListOf(50f, 50f, 50f, 50f))
                        "DST" -> ModuleState("DST", isOn = false, 11, mutableListOf(30f, 50f, 50f))
                        "SNAP" -> ModuleState("SNAP", isOn = false, 0, mutableListOf(50f, 50f, 50f, 50f, 50f))
                        "AMP" -> ModuleState("AMP", isOn = true, 14 /* Ampeg SVT */, mutableListOf(40f, 72f, 68f, 65f, 55f, 75f))
                        "CAB" -> ModuleState("CAB", isOn = true, 2 /* 2x12 */, mutableListOf(50f, 30f, 65f, 75f))
                        "EQ" -> ModuleState("EQ", isOn = true, 2 /* Bass EQ */, mutableListOf(70f, 65f, 50f, 50f, 30f, 65f, 65f))
                        "MOD" -> ModuleState("MOD", isOn = false, 0, mutableListOf(30f, 40f, 50f, 20f))
                        "DLY" -> ModuleState("DLY", isOn = false, 0, mutableListOf(30f, 20f, 50f, 20f))
                        "RVB" -> ModuleState("RVB", isOn = true, 0 /* Room */, mutableListOf(25f, 15f, 40f, 20f))
                        "VOL" -> ModuleState("VOL", isOn = true, 0, mutableListOf(0f, 100f))
                        else -> ModuleState(def.id, true, 0, MutableList(def.params.size) { 50f })
                    }
                }
            )
        ),
        LibraryPresetItem(
            title = "Modern Progressive Bass",
            category = "Bass Grooves",
            description = "Darkglass B7K style aggressive drive with Ampeg SVT grit",
            instrument = "Bass",
            preset = PresetData(
                name = "Prog Dark Bass",
                modules = GP180_MODULE_DEFS.map { def ->
                    when (def.id) {
                        "NR" -> ModuleState("NR", isOn = true, 0, mutableListOf(60f, 35f, 45f))
                        "PRE" -> ModuleState("PRE", isOn = true, 3 /* Limiter */, mutableListOf(55f, 40f, 68f, 55f))
                        "WAH" -> ModuleState("WAH", isOn = false, 0, mutableListOf(50f, 50f, 50f, 50f))
                        "DST" -> ModuleState("DST", isOn = true, 11 /* Black Bass B7K */, mutableListOf(55f, 68f, 70f))
                        "SNAP" -> ModuleState("SNAP", isOn = false, 0, mutableListOf(50f, 50f, 50f, 50f, 50f))
                        "AMP" -> ModuleState("AMP", isOn = true, 14 /* Ampeg SVT */, mutableListOf(60f, 65f, 60f, 65f, 60f, 70f))
                        "CAB" -> ModuleState("CAB", isOn = true, 2 /* 2x12 */, mutableListOf(50f, 35f, 75f, 70f))
                        "EQ" -> ModuleState("EQ", isOn = true, 2 /* Bass EQ */, mutableListOf(65f, 55f, 60f, 55f, 30f, 75f, 65f))
                        "MOD" -> ModuleState("MOD", isOn = false, 2 /* Flanger */, mutableListOf(35f, 40f, 50f, 25f))
                        "DLY" -> ModuleState("DLY", isOn = false, 0, mutableListOf(30f, 20f, 50f, 20f))
                        "RVB" -> ModuleState("RVB", isOn = true, 0, mutableListOf(25f, 20f, 40f, 20f))
                        "VOL" -> ModuleState("VOL", isOn = true, 0, mutableListOf(0f, 100f))
                        else -> ModuleState(def.id, true, 0, MutableList(def.params.size) { 50f })
                    }
                }
            )
        )
    )
}
