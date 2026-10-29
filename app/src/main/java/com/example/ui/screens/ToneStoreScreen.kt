package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LibraryPresetItem
import com.example.data.model.PresetData
import com.example.data.model.PresetLibrary
import com.example.ui.theme.StatusBlue
import com.example.ui.theme.StudioAccent
import com.example.ui.theme.StudioBg
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCard
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.StudioText
import com.example.ui.theme.StudioTextMuted

@Composable
fun ToneStoreScreen(
    onLoadPreset: (PresetData) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("Semua") }
    val categories = listOf("Semua", "Rock / Metal", "Blues / Crunch", "Clean / Ambient", "Bass Grooves")

    val filteredList = remember(selectedCategory) {
        if (selectedCategory == "Semua") PresetLibrary.BUILTIN_PRESETS
        else PresetLibrary.BUILTIN_PRESETS.filter { it.category == selectedCategory }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Koleksi Preset Nada (Tone Store)",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = StudioText,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = "Preset siap pakai yang dikurasi khusus untuk pedal Valeton GP-180. Ketuk 'Muat' untuk mengedit atau mengekspor ke .prst.",
                fontSize = 12.sp,
                color = StudioTextMuted,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Category Filter Pills
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = cat == selectedCategory
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isSelected) StudioAccent else StudioCardElevated)
                            .border(1.dp, if (isSelected) StudioAccent else StudioBorder, RoundedCornerShape(14.dp))
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = cat,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) StudioBg else StudioText
                        )
                    }
                }
            }
        }

        items(filteredList) { item ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("preset_card_${item.title}"),
                colors = CardDefaults.cardColors(containerColor = StudioCard),
                shape = RoundedCornerShape(12.dp),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(StudioBorder))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudioText
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (item.instrument == "Bass") StatusBlue.copy(alpha = 0.2f) else StudioAccent.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = item.instrument.uppercase(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.instrument == "Bass") StatusBlue else StudioAccent
                                )
                            }
                        }

                        Text(
                            text = item.description,
                            fontSize = 11.sp,
                            color = StudioTextMuted,
                            modifier = Modifier.padding(top = 3.dp)
                        )
                    }

                    Button(
                        onClick = { onLoadPreset(item.preset) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StudioCardElevated,
                            contentColor = StudioAccent
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("load_button_${item.title}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Muat",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
