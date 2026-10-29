package com.example.data.parser

import android.util.Base64
import com.example.data.model.GP180_MODULE_DEFS
import com.example.data.model.ModuleState
import com.example.data.model.PresetData
import java.nio.ByteBuffer
import java.nio.ByteOrder

object Gp180BinaryCodec {

    private const val TPL_BASE64 =
        "ETBkBGQAAAAQMFgEABWlJ///DAAQDA8DAwMEECgAAMggMFAAeAAyAAAAAABIeXN0ZXJpYUZ1enoAAAAAAAAAAAAAAAAAAAAAAAAA" +
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwMDwDBQABAgMEBgcICQoLAQAAAAAAAAAAAEhCAABIQgAA" +
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAABgAAyEIAAAAAAAAAAAAAAAAA" +
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAAAAiAAADAACMQgAASEIAAIxCAAAAAAAAAAAAAAAA" +
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMAAAYAAMhCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAAAAcwAABwAAIEIAAHBCAABIQgAAgD8AAEhCAACgQgAAAAAAAAAAAAAAAAAA" +
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAAAAdAAAAAAAMQgAAyEEAAHBCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
        "AAAAAAAAAAAAAAAAAAAAAQAAABAAABoAAAAAAABIQgAAAAAAAAAAAAAAAAAASEIAwNpFAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
        "AAAAAAAAAAAAAAAAAwAABgAAyEIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
        "AAAAAAADAAAGAADIQgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAMA" +
        "AAYAAMhCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAwAABgAAyEIA" +
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEAAAADAAAGAADIQgAAAAAAAAAA" +
        "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAUDAMAAwAAAANAAAA//8AAGAwcAALAAAAAAAA" +
        "AAAAyEL//wAAAAAAAAAAyEL//wAAAAAAAAAAyEL//wAAAAAAAAAAyEL//wAAAAAAAAAAyEL//wAAAAAAAAAAyEL//wAAAAAAAAAA" +
        "yEL//wAAAAAAAAAAyEL//wAAAAAAAAAAyEIAAAAAcDAEAAAAAACAMCQAdQgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
        "AAAA"

    val DEFAULT_TEMPLATE_BYTES: ByteArray by lazy {
        try {
            java.util.Base64.getDecoder().decode(TPL_BASE64)
        } catch (e: Throwable) {
            android.util.Base64.decode(TPL_BASE64, android.util.Base64.DEFAULT)
        }
    }

    private val FILE_SLOT_INDICES = intArrayOf(1, 2, 3, 4, 5, 0, 6, 7, 8, 9, 10, 11)
    private const val PLACEHOLDER_CODE = 0x06000003
    private const val NRC_CODE = 0x1d
    private const val DSC_CODE = 0x03000022

    data class BuildResult(
        val bytes: ByteArray,
        val sanitizedName: String,
        val writtenModules: List<String>,
        val skippedModules: List<String>
    )

    fun build(preset: PresetData): BuildResult {
        val base = preset.rawTemplateBytes?.copyOf() ?: DEFAULT_TEMPLATE_BYTES.copyOf()
        val buffer = ByteBuffer.wrap(base).order(ByteOrder.LITTLE_ENDIAN)

        // Sanitize name: ASCII printable 0x20..0x7E, max 16 chars
        val cleanName = preset.name.ifBlank { "Preset" }
            .map { if (it.code in 0x20..0x7E) it else '?' }
            .take(16)
            .joinToString("")

        // Clear name area 0x2c..0x74
        for (i in 0x2c until 0x74) {
            base[i] = 0
        }
        for (i in cleanName.indices) {
            base[0x2c + i] = cleanName[i].code.toByte()
        }

        val okList = mutableListOf<String>()
        val skipList = mutableListOf<String>()

        for (f in 0 until 12) {
            val offset = 0x84 + 68 * f
            val modIndex = FILE_SLOT_INDICES[f]
            val modDef = GP180_MODULE_DEFS[modIndex]
            val modState = preset.modules.find { it.id == modDef.id } ?: continue

            val code = buffer.getInt(offset + 4)
            if (code == PLACEHOLDER_CODE && f != 11) {
                if (modState.isOn) {
                    skipList.add(modDef.id)
                }
                continue
            }

            buffer.putInt(offset, if (modState.isOn) 1 else 0)
            okList.add(modDef.id)

            if (code == NRC_CODE || code == DSC_CODE) {
                for (k in 0 until minOf(3, modState.values.size)) {
                    val valueRound = Math.round(modState.values[k]).toFloat()
                    buffer.putFloat(offset + 8 + 4 * k, valueRound)
                }
            }
        }

        return BuildResult(
            bytes = base,
            sanitizedName = cleanName,
            writtenModules = okList,
            skippedModules = skipList
        )
    }

    fun parse(bytes: ByteArray): PresetData {
        if (bytes.size < 0x3b4) {
            throw IllegalArgumentException("File size is too small for GP-180 preset (${bytes.size} bytes)")
        }
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)

        val magic1 = buffer.getShort(0).toInt() and 0xFFFF
        val magic2 = buffer.getShort(0x74).toInt() and 0xFFFF
        if (magic1 != 0x3011 || magic2 != 0x3030) {
            throw IllegalArgumentException("Invalid GP-180 magic header: 0x${magic1.toString(16)}, 0x${magic2.toString(16)}")
        }

        val nameBuilder = StringBuilder()
        for (i in 0x2c until 0x74) {
            val b = bytes[i].toInt()
            if (b == 0) break
            nameBuilder.append(b.toChar())
        }
        val name = nameBuilder.toString().ifBlank { "Imported Preset" }

        val modules = GP180_MODULE_DEFS.map { def ->
            ModuleState(
                id = def.id,
                isOn = true,
                selectedModelIndex = 0,
                values = MutableList(def.params.size) { 50f }
            )
        }.toMutableList()

        for (f in 0 until 12) {
            val offset = 0x84 + 68 * f
            val modIndex = FILE_SLOT_INDICES[f]
            val modDef = GP180_MODULE_DEFS[modIndex]
            val modState = modules[modIndex]

            val isOnInt = buffer.getInt(offset)
            modState.isOn = isOnInt == 1

            val code = buffer.getInt(offset + 4)
            if (code == NRC_CODE || code == DSC_CODE) {
                for (k in 0 until minOf(3, modState.values.size)) {
                    val floatVal = buffer.getFloat(offset + 8 + 4 * k)
                    modState.values[k] = floatVal.coerceIn(0f, 100f)
                }
            }
        }

        return PresetData(
            name = name,
            modules = modules,
            rawTemplateBytes = bytes.copyOf()
        )
    }
}
