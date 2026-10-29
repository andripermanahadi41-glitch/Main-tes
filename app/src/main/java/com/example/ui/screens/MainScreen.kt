package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.example.ui.components.LauncherIconCustomizerCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ViewList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.GP180_MODULE_DEFS
import com.example.ui.components.ModuleEditorCard
import com.example.ui.components.PresetFileActionsBar
import com.example.ui.components.PresetGeneratorCard
import com.example.ui.components.RenamePresetDialog
import com.example.ui.components.SignalChainBar
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StudioAccent
import com.example.ui.theme.StudioBg
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCard
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.StudioText
import com.example.ui.theme.StudioTextMuted
import com.example.ui.viewmodel.ToneBuilderViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: ToneBuilderViewModel
) {
    val context = LocalContext.current

    val currentPreset by viewModel.currentPreset.collectAsStateWithLifecycle()
    val selectedModuleIndex by viewModel.selectedModuleIndex.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val searchBand by viewModel.searchBand.collectAsStateWithLifecycle()
    val searchSong by viewModel.searchSong.collectAsStateWithLifecycle()
    val searchLink by viewModel.searchLink.collectAsStateWithLifecycle()
    val selectedInstrument by viewModel.selectedInstrument.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()
    val customApiKey by viewModel.customApiKey.collectAsStateWithLifecycle()
    val selectedGeminiModel by viewModel.selectedGeminiModel.collectAsStateWithLifecycle()
    val isBluetoothConnected by viewModel.isBluetoothConnected.collectAsStateWithLifecycle()
    val isUsbConnected by viewModel.isUsbConnected.collectAsStateWithLifecycle()
    val customIconBitmap by viewModel.customIconBitmap.collectAsStateWithLifecycle()
    val customIconInfo by viewModel.customIconInfo.collectAsStateWithLifecycle()

    var showRenameDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle system back button: return to Tab 0 if in secondary tab
    BackHandler(enabled = currentTab != 0) {
        viewModel.setTab(0)
    }

    // Show status message toast/snackbar
    LaunchedEffect(statusMessage) {
        statusMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearStatusMessage()
        }
    }

    // Open file launcher (.prst)
    val openDocLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.openInputStream(it)?.use { stream ->
                    val bytes = stream.readBytes()
                    viewModel.openPresetFromBytes(bytes)
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal membuka file: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Save file launcher (.prst)
    val createDocLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/octet-stream")
    ) { uri: Uri? ->
        uri?.let {
            try {
                val buildResult = viewModel.buildPresetBytes()
                context.contentResolver.openOutputStream(it)?.use { stream ->
                    stream.write(buildResult.bytes)
                }
                Toast.makeText(context, "${buildResult.sanitizedName}.prst berhasil disimpan!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Gagal menyimpan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        containerColor = StudioBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // Header Bar
            Surface(
                color = StudioBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // App Logo Icon (shows custom uploaded PNG if set)
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(StudioCard)
                            .border(1.dp, if (customIconBitmap != null) StudioAccent else StudioBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        val customBmp = customIconBitmap
                        if (customBmp != null) {
                            Image(
                                bitmap = customBmp.asImageBitmap(),
                                contentDescription = "Ikon Peluncur Kustom",
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.valeton_gp180_icon_1793262145330),
                                contentDescription = "Logo Valeton GP-180",
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Preset Name Title (Clickable to Rename)
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showRenameDialog = true }
                            .padding(vertical = 4.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentPreset.name,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = StudioText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Ubah nama preset",
                            tint = StudioTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    // Hardware Status Chips
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Bluetooth Chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(StudioCard)
                                .clickable { viewModel.toggleBluetooth() }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (isBluetoothConnected) "● BT" else "○ BT",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isBluetoothConnected) StatusGreen else StudioTextMuted
                            )
                        }

                        // USB Chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(StudioCard)
                                .clickable { viewModel.toggleUsb() }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (isUsbConnected) "● USB" else "○ USB",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isUsbConnected) StatusGreen else StudioTextMuted
                            )
                        }

                        // Settings Icon
                        IconButton(
                            onClick = { viewModel.setTab(3) },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Pengaturan",
                                tint = if (currentTab == 3) StudioAccent else StudioTextMuted
                            )
                        }
                    }
                }
            }
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars),
                containerColor = StudioCard,
                contentColor = StudioText
            ) {
                NavigationBarItem(
                    selected = currentTab == 0,
                    onClick = { viewModel.setTab(0) },
                    icon = { Icon(Icons.Default.GridView, contentDescription = "Preset") },
                    label = { Text("Preset", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = StudioBg,
                        selectedTextColor = StudioAccent,
                        indicatorColor = StudioAccent,
                        unselectedIconColor = StudioTextMuted,
                        unselectedTextColor = StudioTextMuted
                    )
                )

                NavigationBarItem(
                    selected = currentTab == 1,
                    onClick = { viewModel.setTab(1) },
                    icon = { Icon(Icons.AutoMirrored.Filled.ViewList, contentDescription = "Effects") },
                    label = { Text("Effects", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = StudioBg,
                        selectedTextColor = StudioAccent,
                        indicatorColor = StudioAccent,
                        unselectedIconColor = StudioTextMuted,
                        unselectedTextColor = StudioTextMuted
                    )
                )

                NavigationBarItem(
                    selected = currentTab == 2,
                    onClick = { viewModel.setTab(2) },
                    icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "Store") },
                    label = { Text("Store", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = StudioBg,
                        selectedTextColor = StudioAccent,
                        indicatorColor = StudioAccent,
                        unselectedIconColor = StudioTextMuted,
                        unselectedTextColor = StudioTextMuted
                    )
                )

                NavigationBarItem(
                    selected = currentTab == 3,
                    onClick = { viewModel.setTab(3) },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Info") },
                    label = { Text("Info", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = StudioBg,
                        selectedTextColor = StudioAccent,
                        indicatorColor = StudioAccent,
                        unselectedIconColor = StudioTextMuted,
                        unselectedTextColor = StudioTextMuted
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                0 -> {
                    // Main Preset Builder Tab
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                    ) {
                        // AI Song Preset Generator
                        PresetGeneratorCard(
                            band = searchBand,
                            onBandChange = viewModel::setSearchBand,
                            song = searchSong,
                            onSongChange = viewModel::setSearchSong,
                            link = searchLink,
                            onLinkChange = viewModel::setSearchLink,
                            instrument = selectedInstrument,
                            onInstrumentChange = viewModel::setInstrument,
                            isGenerating = isGenerating,
                            onGenerate = viewModel::generatePreset,
                            onSelectSample = viewModel::loadSample
                        )

                        // Custom Launcher Icon Uploader Component
                        LauncherIconCustomizerCard(
                            customBitmap = customIconBitmap,
                            customIconInfo = customIconInfo,
                            onUploadIcon = viewModel::setCustomIcon,
                            onResetIcon = viewModel::resetCustomIcon
                        )

                        // 12-Module Signal Chain
                        SignalChainBar(
                            modules = currentPreset.modules,
                            selectedIndex = selectedModuleIndex,
                            onSelectModule = viewModel::selectModule
                        )

                        // Selected Module Knobs & Model Card
                        val currentDef = GP180_MODULE_DEFS[selectedModuleIndex]
                        val currentModState = currentPreset.modules[selectedModuleIndex]

                        ModuleEditorCard(
                            moduleDef = currentDef,
                            moduleState = currentModState,
                            onTogglePower = { viewModel.toggleModule(selectedModuleIndex) },
                            onStepModel = viewModel::stepModel,
                            onSelectModel = { modelIdx -> viewModel.setModuleModel(selectedModuleIndex, modelIdx) },
                            onKnobChange = { paramIdx, newVal ->
                                viewModel.updateKnobValue(selectedModuleIndex, paramIdx, newVal)
                            }
                        )

                        // File Actions Bar: Open, Save, Share
                        PresetFileActionsBar(
                            onOpenFile = {
                                openDocLauncher.launch(arrayOf("*/*"))
                            },
                            onSaveFile = {
                                val cleanName = currentPreset.name.replace(Regex("[^a-zA-Z0-9_-]"), "_")
                                createDocLauncher.launch("$cleanName.prst")
                            },
                            onShareFile = {
                                try {
                                    val buildResult = viewModel.buildPresetBytes()
                                    val safeName = buildResult.sanitizedName.replace(Regex("[^a-zA-Z0-9_-]"), "_").ifBlank { "preset" }
                                    val file = File(context.cacheDir, "$safeName.prst")
                                    file.writeBytes(buildResult.bytes)
                                    val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "application/octet-stream"
                                        putExtra(Intent.EXTRA_STREAM, uri)
                                        putExtra(Intent.EXTRA_SUBJECT, "GP-180 Preset: ${buildResult.sanitizedName}")
                                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Bagikan preset .prst"))
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Gagal membagikan: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                1 -> {
                    // Effects List Tab
                    EffectsListScreen(
                        modules = currentPreset.modules,
                        onToggleModule = viewModel::toggleModule,
                        onSelectAndEdit = { modIdx ->
                            viewModel.selectModule(modIdx)
                            viewModel.setTab(0)
                        }
                    )
                }

                2 -> {
                    // Tone Store Tab
                    ToneStoreScreen(
                        onLoadPreset = viewModel::loadPreset
                    )
                }

                3 -> {
                    // Settings & Info Tab
                    SettingsScreen(
                        currentApiKey = customApiKey,
                        onSaveApiKey = viewModel::setApiKey,
                        selectedModel = selectedGeminiModel,
                        onSelectModel = viewModel::setGeminiModel,
                        customBitmap = customIconBitmap,
                        customIconInfo = customIconInfo,
                        onUploadIcon = viewModel::setCustomIcon,
                        onResetIcon = viewModel::resetCustomIcon
                    )
                }
            }
        }
    }

    // Rename Modal Dialog
    if (showRenameDialog) {
        RenamePresetDialog(
            initialName = currentPreset.name,
            onDismiss = { showRenameDialog = false },
            onConfirm = { newName ->
                viewModel.setPresetName(newName)
            }
        )
    }
}
