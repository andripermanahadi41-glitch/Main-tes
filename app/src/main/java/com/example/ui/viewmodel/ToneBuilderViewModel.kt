package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.GP180_MODULE_DEFS
import com.example.data.model.PresetData
import com.example.data.model.createDefaultPreset
import com.example.data.parser.Gp180BinaryCodec
import com.example.data.service.GeminiToneService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ToneBuilderViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("gp180_prefs", Context.MODE_PRIVATE)

    private val _currentPreset = MutableStateFlow(createDefaultPreset())
    val currentPreset: StateFlow<PresetData> = _currentPreset.asStateFlow()

    private val _selectedModuleIndex = MutableStateFlow(5) // AMP by default
    val selectedModuleIndex: StateFlow<Int> = _selectedModuleIndex.asStateFlow()

    private val _currentTab = MutableStateFlow(0) // 0: Preset, 1: Effects, 2: Store, 3: Settings
    val currentTab: StateFlow<Int> = _currentTab.asStateFlow()

    private val _searchBand = MutableStateFlow("")
    val searchBand: StateFlow<String> = _searchBand.asStateFlow()

    private val _searchSong = MutableStateFlow("")
    val searchSong: StateFlow<String> = _searchSong.asStateFlow()

    private val _searchLink = MutableStateFlow("")
    val searchLink: StateFlow<String> = _searchLink.asStateFlow()

    private val _selectedInstrument = MutableStateFlow("gitar")
    val selectedInstrument: StateFlow<String> = _selectedInstrument.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>("Siap. Pilih modul untuk mengubah parameter.")
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _customApiKey = MutableStateFlow(prefs.getString("gemini_key", "").orEmpty())
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    private val _selectedGeminiModel = MutableStateFlow(prefs.getString("gemini_model", "gemini-2.5-flash").orEmpty())
    val selectedGeminiModel: StateFlow<String> = _selectedGeminiModel.asStateFlow()

    private val _isBluetoothConnected = MutableStateFlow(false)
    val isBluetoothConnected: StateFlow<Boolean> = _isBluetoothConnected.asStateFlow()

    private val _isUsbConnected = MutableStateFlow(false)
    val isUsbConnected: StateFlow<Boolean> = _isUsbConnected.asStateFlow()

    // Custom Launcher Icon State
    private val iconFile = java.io.File(application.filesDir, "custom_launcher_icon.png")
    private val _customIconBitmap = MutableStateFlow<android.graphics.Bitmap?>(null)
    val customIconBitmap: StateFlow<android.graphics.Bitmap?> = _customIconBitmap.asStateFlow()

    private val _customIconInfo = MutableStateFlow<String?>(null)
    val customIconInfo: StateFlow<String?> = _customIconInfo.asStateFlow()

    init {
        loadSavedCustomIcon()
    }

    private fun loadSavedCustomIcon() {
        if (iconFile.exists()) {
            try {
                val bitmap = android.graphics.BitmapFactory.decodeFile(iconFile.absolutePath)
                if (bitmap != null) {
                    _customIconBitmap.value = bitmap
                    _customIconInfo.value = "${bitmap.width}x${bitmap.height} px (PNG Kustom)"
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    fun setCustomIcon(bitmap: android.graphics.Bitmap, fileName: String = "icon.png") {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                java.io.FileOutputStream(iconFile).use { out ->
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, out)
                }
                _customIconBitmap.value = bitmap
                _customIconInfo.value = "$fileName (${bitmap.width}x${bitmap.height} px)"
                _statusMessage.value = "Ikon peluncur kustom ($fileName) berhasil dipasang!"
            } catch (e: Exception) {
                _statusMessage.value = "Gagal menyimpan ikon: ${e.message}"
            }
        }
    }

    fun resetCustomIcon() {
        if (iconFile.exists()) {
            iconFile.delete()
        }
        _customIconBitmap.value = null
        _customIconInfo.value = null
        _statusMessage.value = "Ikon peluncur dikembalikan ke ikon pedal bawaan."
    }

    private val toneService = GeminiToneService(apiKeyProvider = { _customApiKey.value })

    fun selectModule(index: Int) {
        if (index in GP180_MODULE_DEFS.indices) {
            _selectedModuleIndex.value = index
        }
    }

    fun toggleModule(index: Int) {
        if (index !in GP180_MODULE_DEFS.indices) return
        _currentPreset.update { preset ->
            val updatedModules = preset.modules.mapIndexed { idx, mod ->
                if (idx == index) mod.copyState().apply { isOn = !isOn }
                else mod
            }
            preset.copy(modules = updatedModules)
        }
        val modId = GP180_MODULE_DEFS[index].id
        val isOn = _currentPreset.value.modules[index].isOn
        _statusMessage.value = "Modul $modId ${if (isOn) "diaktifkan (ON)" else "dimatikan (OFF)"}"
    }

    fun setModuleModel(moduleIndex: Int, modelIndex: Int) {
        if (moduleIndex !in GP180_MODULE_DEFS.indices) return
        val models = GP180_MODULE_DEFS[moduleIndex].models
        if (modelIndex !in models.indices) return

        _currentPreset.update { preset ->
            val updatedModules = preset.modules.mapIndexed { idx, mod ->
                if (idx == moduleIndex) mod.copyState().apply { selectedModelIndex = modelIndex }
                else mod
            }
            preset.copy(modules = updatedModules)
        }
        val modName = models[modelIndex]
        _statusMessage.value = "Model ${GP180_MODULE_DEFS[moduleIndex].id} diubah ke $modName"
    }

    fun stepModel(delta: Int) {
        val currIdx = _selectedModuleIndex.value
        val models = GP180_MODULE_DEFS[currIdx].models
        val currentMod = _currentPreset.value.modules[currIdx]
        val newIndex = (currentMod.selectedModelIndex + delta + models.size) % models.size
        setModuleModel(currIdx, newIndex)
    }

    fun updateKnobValue(moduleIndex: Int, paramIndex: Int, value: Float) {
        if (moduleIndex !in GP180_MODULE_DEFS.indices) return
        _currentPreset.update { preset ->
            val updatedModules = preset.modules.mapIndexed { idx, mod ->
                if (idx == moduleIndex) {
                    val newValues = mod.values.toMutableList()
                    if (paramIndex in newValues.indices) {
                        newValues[paramIndex] = value.coerceIn(0f, 100f)
                    }
                    mod.copy(values = newValues)
                } else mod
            }
            preset.copy(modules = updatedModules)
        }
    }

    fun setPresetName(name: String) {
        val clean = name.take(16)
        _currentPreset.update { it.copy(name = clean) }
    }

    fun setSearchBand(band: String) {
        _searchBand.value = band
    }

    fun setSearchSong(song: String) {
        _searchSong.value = song
    }

    fun setSearchLink(link: String) {
        _searchLink.value = link
    }

    fun setInstrument(instrument: String) {
        _selectedInstrument.value = instrument.lowercase()
    }

    fun setTab(tab: Int) {
        _currentTab.value = tab
    }

    fun loadSample(band: String, song: String) {
        _searchBand.value = band
        _searchSong.value = song
    }

    fun generatePreset() {
        val band = _searchBand.value.trim()
        val song = _searchSong.value.trim()
        val link = _searchLink.value.trim()

        if (band.isBlank() && song.isBlank() && link.isBlank()) {
            _statusMessage.value = "Ketik nama band dan judul lagu terlebih dahulu."
            return
        }

        viewModelScope.launch {
            _isGenerating.value = true
            _statusMessage.value = "Menyusun preset tone GP-180..."

            val result = toneService.generatePreset(
                band = band,
                title = song,
                instrument = _selectedInstrument.value,
                preferredModel = _selectedGeminiModel.value
            )

            result.onSuccess { newPreset ->
                _currentPreset.value = newPreset
                _statusMessage.value = "Preset \"${newPreset.name}\" berhasil disusun!"
            }.onFailure { err ->
                _statusMessage.value = "Gagal menyusun preset: ${err.message}"
            }

            _isGenerating.value = false
        }
    }

    fun loadPreset(preset: PresetData) {
        _currentPreset.value = preset
        _currentTab.value = 0
        _statusMessage.value = "Preset \"${preset.name}\" berhasil dimuat."
    }

    fun openPresetFromBytes(bytes: ByteArray): Boolean {
        return try {
            val parsed = Gp180BinaryCodec.parse(bytes)
            _currentPreset.value = parsed
            _statusMessage.value = "File .prst \"${parsed.name}\" berhasil dibuka!"
            true
        } catch (e: Exception) {
            _statusMessage.value = "Gagal membuka file .prst: ${e.message}"
            false
        }
    }

    fun buildPresetBytes(): Gp180BinaryCodec.BuildResult {
        return Gp180BinaryCodec.build(_currentPreset.value)
    }

    fun setApiKey(key: String) {
        _customApiKey.value = key.trim()
        prefs.edit().putString("gemini_key", key.trim()).apply()
        _statusMessage.value = "Gemini API Key disimpan."
    }

    fun setGeminiModel(model: String) {
        _selectedGeminiModel.value = model
        prefs.edit().putString("gemini_model", model).apply()
        _statusMessage.value = "Model AI diubah ke $model"
    }

    fun toggleBluetooth() {
        _isBluetoothConnected.value = !_isBluetoothConnected.value
        _statusMessage.value = if (_isBluetoothConnected.value) {
            "Simulasi Bluetooth BLE MIDI: Terhubung ke Valeton GP-180"
        } else {
            "Bluetooth terputus."
        }
    }

    fun toggleUsb() {
        _isUsbConnected.value = !_isUsbConnected.value
        _statusMessage.value = if (_isUsbConnected.value) {
            "USB MIDI OTG: Terdeteksi perangkat GP-180"
        } else {
            "Kabel USB MIDI terlepas."
        }
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }
}
