package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StudioAccent
import com.example.ui.theme.StudioBg
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCard
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.StudioText
import com.example.ui.theme.StudioTextMuted

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentApiKey: String,
    onSaveApiKey: (String) -> Unit,
    selectedModel: String,
    onSelectModel: (String) -> Unit,
    customBitmap: android.graphics.Bitmap? = null,
    customIconInfo: String? = null,
    onUploadIcon: ((android.graphics.Bitmap, String) -> Unit)? = null,
    onResetIcon: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var apiKeyInput by remember(currentApiKey) { mutableStateOf(currentApiKey) }
    var isKeyVisible by remember { mutableStateOf(false) }
    var isModelDropdownExpanded by remember { mutableStateOf(false) }
    val models = listOf("gemini-2.5-flash", "gemini-2.5-pro", "gemini-2.0-flash")

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = "Pengaturan & Konfigurasi AI",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = StudioText
        )

        if (onUploadIcon != null) {
            com.example.ui.components.LauncherIconCustomizerCard(
                customBitmap = customBitmap,
                customIconInfo = customIconInfo,
                onUploadIcon = onUploadIcon,
                onResetIcon = { onResetIcon?.invoke() }
            )
        }

        // AI Provider & Key Card
        Card(
            colors = CardDefaults.cardColors(containerColor = StudioCard),
            shape = RoundedCornerShape(12.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(StudioBorder))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = null,
                        tint = StudioAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Google Gemini AI Engine",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioText
                    )
                }

                Text(
                    text = "Aplikasi ini memiliki algoritma Smart Tone offline bawaan. Anda juga dapat menambahkan Google Gemini API Key sendiri untuk hasil tone AI tingkat lanjut.",
                    fontSize = 12.sp,
                    color = StudioTextMuted,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                // Model Selector
                Text(
                    text = "Pilih Model Gemini",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudioTextMuted,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                ExposedDropdownMenuBox(
                    expanded = isModelDropdownExpanded,
                    onExpandedChange = { isModelDropdownExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedModel,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isModelDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(androidx.compose.material3.MenuAnchorType.PrimaryNotEditable),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = StudioAccent,
                            unfocusedBorderColor = StudioBorder,
                            focusedTextColor = StudioText,
                            unfocusedTextColor = StudioText,
                            focusedContainerColor = StudioCardElevated,
                            unfocusedContainerColor = StudioCardElevated
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )

                    ExposedDropdownMenu(
                        expanded = isModelDropdownExpanded,
                        onDismissRequest = { isModelDropdownExpanded = false },
                        modifier = Modifier.background(StudioCardElevated)
                    ) {
                        models.forEach { modelName ->
                            DropdownMenuItem(
                                text = { Text(modelName, color = StudioText) },
                                onClick = {
                                    onSelectModel(modelName)
                                    isModelDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // API Key Field
                Text(
                    text = "Gemini API Key (Opsional)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = StudioTextMuted,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                OutlinedTextField(
                    value = apiKeyInput,
                    onValueChange = { apiKeyInput = it },
                    placeholder = { Text("Tempel Gemini API Key di sini", fontSize = 12.sp, color = StudioTextMuted) },
                    visualTransformation = if (isKeyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isKeyVisible = !isKeyVisible }) {
                            Icon(
                                imageVector = if (isKeyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isKeyVisible) "Sembunyikan" else "Tampilkan",
                                tint = StudioTextMuted
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("api_key_input"),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StudioAccent,
                        unfocusedBorderColor = StudioBorder,
                        focusedTextColor = StudioText,
                        unfocusedTextColor = StudioText,
                        focusedContainerColor = StudioCardElevated,
                        unfocusedContainerColor = StudioCardElevated
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { onSaveApiKey(apiKeyInput) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_api_key_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StudioAccent,
                        contentColor = StudioBg
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Simpan API Key",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Hardware & File Format Info Card
        Card(
            colors = CardDefaults.cardColors(containerColor = StudioCard),
            shape = RoundedCornerShape(12.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(StudioBorder))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Cable,
                        contentDescription = null,
                        tint = StatusGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Koneksi Pedal & Format .prst GP-180",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioText
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "• Format File .prst:\n" +
                            "File preset Valeton GP-180 berukuran 948 byte dengan magic header biner 0x3011 & 0x3030. Aplikasi ini menulis langsung nilai float32 modul efek dan nama preset agar kompatibel 100% dengan firmware GP-180.\n\n" +
                            "• Cara Impor ke Pedal:\n" +
                            "1. Simpan preset ke ponsel dengan tombol 'Simpan .prst'.\n" +
                            "2. Hubungkan pedal ke PC atau ponsel via kabel USB (USB OTG).\n" +
                            "3. Buka software resmi Valeton GP-180 Software lalu klik Import Preset (.prst).\n" +
                            "4. Nada langsung tersinkron ke slot pedal!",
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    color = StudioTextMuted
                )
            }
        }

        // About & Version Card
        Card(
            colors = CardDefaults.cardColors(containerColor = StudioCard),
            shape = RoundedCornerShape(12.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(StudioBorder))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = StudioAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tentang GP180 Tone Builder",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioText
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Aplikasi editor nada multi-efek Valeton GP-180 untuk gitar & bass elektrik dengan integrasi Gemini AI dan dukungan biner .prst.",
                    fontSize = 12.sp,
                    color = StudioTextMuted
                )

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Versi: 1.0.0 (Native Android Jetpack Compose)",
                    fontSize = 11.sp,
                    color = StudioAccent
                )
            }
        }

        Spacer(modifier = Modifier.height(60.dp))
    }
}
