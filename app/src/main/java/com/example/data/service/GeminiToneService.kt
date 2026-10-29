package com.example.data.service

import com.example.BuildConfig
import com.example.data.model.GP180_MODULE_DEFS
import com.example.data.model.ModuleState
import com.example.data.model.PresetData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiToneService(
    private val apiKeyProvider: () -> String?
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generatePreset(
        band: String,
        title: String,
        instrument: String, // "gitar" or "bass"
        preferredModel: String = "gemini-2.5-flash"
    ): Result<PresetData> = withContext(Dispatchers.IO) {
        val songName = if (band.isNotBlank() && title.isNotBlank()) "$band - $title" else (band.ifBlank { title })
        val fromBuildConfig: String? = try {
            val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
            field.get(null) as? String
        } catch (e: Throwable) {
            null
        }
        val effectiveApiKey = apiKeyProvider()?.takeIf { it.isNotBlank() }
            ?: fromBuildConfig?.takeIf { it.isNotBlank() && !it.contains("MY_GEMINI_API_KEY") }

        if (effectiveApiKey.isNullOrBlank()) {
            // Intelligent fallback preset based on song style and instrument
            return@withContext Result.success(generateSmartFallbackPreset(band, title, instrument))
        }

        try {
            val moduleSpec = GP180_MODULE_DEFS.joinToString(", ") { def ->
                "${def.id}:${def.params.size}[${def.models.joinToString("|")}]"
            }

            val isBass = instrument.lowercase() == "bass"
            val prompt = buildString {
                append("Kamu ahli tone audio profesional ").append(if (isBass) "bass" else "gitar").append(". ")
                append("Buat preset pedal Valeton GP-180 untuk ").append(if (isBass) "BASS" else "GITAR")
                append(" pada lagu (band - judul): \"$songName\". ")
                if (isBass) {
                    append("PILIH model bass: Ampeg SVT (Bass), Fender Bassman (Bass), Bass EQ, Black Bass (B7K), Bass Hammer, dan jangan gunakan amp gitar high-gain tebal. ")
                }
                append("Balas HANYA JSON murni tanpa markdown dengan skema: ")
                append("""{"name":"Nama Preset Singkat (max 16 char)","on":["NR","PRE","AMP",...],"vals":{"AMP":[65,60,40,50,40,60],...},"models":{"AMP":"Marshall JCM800",...}}""")
                append(". Daftar modul yang tersedia [format ID:jumlah_parameter[pilihan_model]]: ")
                append(moduleSpec)
            }

            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", prompt))
                        })
                    })
                }
                put("contents", contentsArray)
                put("generationConfig", JSONObject().apply {
                    put("responseMimeType", "application/json")
                })
            }

            val url = "https://generativelanguage.googleapis.com/v1beta/models/$preferredModel:generateContent?key=$effectiveApiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            if (!response.isSuccessful) {
                // If API key returned an error (e.g. quota or invalid key), provide smart fallback with descriptive note
                val fallback = generateSmartFallbackPreset(band, title, instrument)
                return@withContext Result.success(fallback.copy(name = "${fallback.name}*"))
            }

            val body = response.body?.string().orEmpty()
            val root = JSONObject(body)
            val candidates = root.optJSONArray("candidates")
            if (candidates == null || candidates.length() == 0) {
                return@withContext Result.success(generateSmartFallbackPreset(band, title, instrument))
            }

            val textPart = candidates.getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val parsedJson = JSONObject(textPart)
            val parsedPreset = mapJsonToPreset(parsedJson, songName)
            Result.success(parsedPreset)
        } catch (e: Exception) {
            // Graceful smart fallback on network or parsing errors
            val fallback = generateSmartFallbackPreset(band, title, instrument)
            Result.success(fallback)
        }
    }

    private fun mapJsonToPreset(json: JSONObject, originalQuery: String): PresetData {
        val presetName = json.optString("name", originalQuery.take(16))
            .filter { it.code in 0x20..0x7E }
            .take(16)
            .ifBlank { "AI Preset" }

        val activeIds = mutableSetOf<String>()
        val onArray = json.optJSONArray("on")
        if (onArray != null) {
            for (i in 0 until onArray.length()) {
                activeIds.add(onArray.getString(i))
            }
        }

        val valsObj = json.optJSONObject("vals")
        val modelsObj = json.optJSONObject("models")

        val modules = GP180_MODULE_DEFS.map { def ->
            val isOn = activeIds.contains(def.id)
            val modelName = modelsObj?.optString(def.id)
            val modelIndex = if (!modelName.isNullOrBlank()) {
                val idx = def.models.indexOf(modelName)
                if (idx >= 0) idx else 0
            } else 0

            val values = MutableList(def.params.size) { 50f }
            val paramArray = valsObj?.optJSONArray(def.id)
            if (paramArray != null) {
                for (p in 0 until minOf(paramArray.length(), def.params.size)) {
                    values[p] = paramArray.optDouble(p, 50.0).toFloat().coerceIn(0f, 100f)
                }
            }

            ModuleState(
                id = def.id,
                isOn = isOn,
                selectedModelIndex = modelIndex,
                values = values
            )
        }

        return PresetData(name = presetName, modules = modules)
    }

    fun generateSmartFallbackPreset(band: String, title: String, instrument: String): PresetData {
        val query = "$band $title".lowercase()
        val isBass = instrument.lowercase() == "bass"

        if (isBass) {
            val presetName = if (title.isNotBlank()) title.take(14) else "Bass Punch"
            val isHeavy = query.contains("metal") || query.contains("rock") || query.contains("rage") || query.contains("tool") || query.contains("slayer")
            val isFunk = query.contains("funk") || query.contains("rhcp") || query.contains("chili") || query.contains("queen") || query.contains("disco")

            return PresetData(
                name = presetName.take(16),
                modules = GP180_MODULE_DEFS.map { def ->
                    when (def.id) {
                        "NR" -> ModuleState("NR", isOn = true, selectedModelIndex = 0, values = mutableListOf(50f, 40f, 60f))
                        "PRE" -> ModuleState("PRE", isOn = true, selectedModelIndex = 3 /* Limiter */, values = mutableListOf(60f, 50f, 70f, 55f))
                        "WAH" -> ModuleState("WAH", isOn = false, selectedModelIndex = 0, values = mutableListOf(50f, 50f, 50f, 50f))
                        "DST" -> ModuleState("DST", isOn = isHeavy, selectedModelIndex = 11 /* Black Bass B7K */, values = mutableListOf(45f, 60f, 65f))
                        "SNAP" -> ModuleState("SNAP", isOn = false, selectedModelIndex = 0, values = mutableListOf(50f, 50f, 50f, 50f, 50f))
                        "AMP" -> ModuleState(
                            "AMP",
                            isOn = true,
                            selectedModelIndex = 14 /* Ampeg SVT (Bass) */,
                            values = if (isHeavy) mutableListOf(65f, 70f, 60f, 55f, 50f, 75f)
                            else if (isFunk) mutableListOf(45f, 75f, 68f, 70f, 60f, 70f)
                            else mutableListOf(50f, 65f, 55f, 50f, 45f, 70f)
                        )
                        "CAB" -> ModuleState("CAB", isOn = true, selectedModelIndex = 2 /* 2x12 */, values = mutableListOf(50f, 35f, 65f, 70f))
                        "EQ" -> ModuleState("EQ", isOn = true, selectedModelIndex = 2 /* Bass EQ */, values = mutableListOf(68f, 60f, 55f, 50f, 30f, 70f, 65f))
                        "MOD" -> ModuleState("MOD", isOn = isFunk, selectedModelIndex = 0 /* Chorus */, values = mutableListOf(35f, 45f, 50f, 30f))
                        "DLY" -> ModuleState("DLY", isOn = false, selectedModelIndex = 0, values = mutableListOf(40f, 30f, 50f, 25f))
                        "RVB" -> ModuleState("RVB", isOn = true, selectedModelIndex = 0 /* Room */, values = mutableListOf(30f, 20f, 45f, 25f))
                        "VOL" -> ModuleState("VOL", isOn = true, selectedModelIndex = 0, values = mutableListOf(0f, 100f))
                        else -> ModuleState(def.id, isOn = true, selectedModelIndex = 0, values = MutableList(def.params.size) { 50f })
                    }
                }
            )
        }

        // Guitar Presets
        val isMetal = query.contains("metallica") || query.contains("puppets") || query.contains("metal") || query.contains("pantera") || query.contains("megadeth") || query.contains("avenged")
        val isSolo = query.contains("numb") || query.contains("floyd") || query.contains("solo") || query.contains("lead") || query.contains("slash") || query.contains("santana")
        val isBlues = query.contains("blues") || query.contains("srv") || query.contains("vaughan") || query.contains("clapton") || query.contains("hendrix")
        val isClean = query.contains("bridge") || query.contains("clean") || query.contains("rhcp") || query.contains("ambient") || query.contains("polyphia")

        val presetName = when {
            isMetal -> "Metal Thrash"
            isSolo -> "Comfort Solo"
            isBlues -> "Texas Blues"
            isClean -> "Clean Sparkle"
            title.isNotBlank() -> title.take(14)
            else -> "Guitar Tone"
        }

        return PresetData(
            name = presetName.take(16),
            modules = GP180_MODULE_DEFS.map { def ->
                when (def.id) {
                    "NR" -> ModuleState(
                        "NR",
                        isOn = isMetal || isSolo,
                        selectedModelIndex = if (isSolo) 1 /* Gate 2 Flexible */ else 0 /* Gate 1 ISP Decimator */,
                        values = mutableListOf(65f, 40f, 50f)
                    )
                    "PRE" -> ModuleState("PRE", isOn = isClean || isBlues, selectedModelIndex = 0 /* S-Comp */, values = mutableListOf(55f, 45f, 65f, 50f))
                    "WAH" -> ModuleState("WAH", isOn = false, selectedModelIndex = 0, values = mutableListOf(50f, 50f, 50f, 50f))
                    "DST" -> ModuleState(
                        "DST",
                        isOn = isMetal || isBlues || isSolo,
                        selectedModelIndex = if (isMetal) 1 /* TS9 */ else if (isBlues) 0 /* TS808 */ else 9 /* Big Muff */,
                        values = if (isMetal) mutableListOf(40f, 65f, 75f) else mutableListOf(60f, 55f, 65f)
                    )
                    "SNAP" -> ModuleState("SNAP", isOn = false, selectedModelIndex = 0, values = mutableListOf(50f, 50f, 50f, 50f, 50f))
                    "AMP" -> ModuleState(
                        "AMP",
                        isOn = true,
                        selectedModelIndex = when {
                            isMetal -> 9 /* Peavey 5150 */
                            isSolo -> 7 /* Mesa Dual Rectifier */
                            isBlues -> 5 /* Marshall JCM800 */
                            isClean -> 1 /* Fender 65 Twin Reverb */
                            else -> 5 /* Marshall JCM800 */
                        },
                        values = when {
                            isMetal -> mutableListOf(75f, 68f, 35f, 70f, 65f, 60f)
                            isSolo -> mutableListOf(65f, 63f, 55f, 60f, 55f, 65f)
                            isBlues -> mutableListOf(55f, 50f, 65f, 55f, 50f, 70f)
                            else -> mutableListOf(35f, 55f, 50f, 65f, 45f, 70f)
                        }
                    )
                    "CAB" -> ModuleState(
                        "CAB",
                        isOn = true,
                        selectedModelIndex = if (isMetal) 4 /* 4x12 Mesa */ else 3 /* 4x12 Marshall */,
                        values = mutableListOf(50f, 40f, 75f, 65f)
                    )
                    "EQ" -> ModuleState("EQ", isOn = isMetal, selectedModelIndex = 0, values = mutableListOf(65f, 40f, 65f, 50f, 40f, 80f, 60f))
                    "MOD" -> ModuleState("MOD", isOn = isSolo || isClean, selectedModelIndex = 0 /* Chorus */, values = mutableListOf(40f, 50f, 50f, 35f))
                    "DLY" -> ModuleState("DLY", isOn = isSolo || isClean, selectedModelIndex = 2 /* Tape Delay */, values = mutableListOf(45f, 40f, 50f, 35f))
                    "RVB" -> ModuleState("RVB", isOn = true, selectedModelIndex = if (isSolo) 1 /* Hall */ else 4 /* Spring */, values = mutableListOf(50f, 30f, 50f, 35f))
                    "VOL" -> ModuleState("VOL", isOn = true, selectedModelIndex = 0, values = mutableListOf(0f, 100f))
                    else -> ModuleState(def.id, isOn = true, selectedModelIndex = 0, values = MutableList(def.params.size) { 50f })
                }
            }
        )
    }
}
