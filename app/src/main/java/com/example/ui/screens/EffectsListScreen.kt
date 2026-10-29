package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GP180_MODULE_DEFS
import com.example.data.model.ModuleState
import com.example.ui.theme.StudioAccent
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCard
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.StudioText
import com.example.ui.theme.StudioTextMuted

@Composable
fun EffectsListScreen(
    modules: List<ModuleState>,
    onToggleModule: (Int) -> Unit,
    onSelectAndEdit: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Daftar Efek GP-180",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = StudioText,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = "Ketuk modul untuk membuka kontrol knob dan parameter, atau aktifkan/nonaktifkan modul di sini.",
                fontSize = 12.sp,
                color = StudioTextMuted,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        itemsIndexed(GP180_MODULE_DEFS) { index, def ->
            val state = modules.getOrNull(index) ?: return@itemsIndexed
            val currentModel = def.models.getOrNull(state.selectedModelIndex) ?: def.models.first()

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectAndEdit(index) }
                    .testTag("effect_item_${def.id}"),
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
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(StudioCardElevated),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = def.id,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = def.composeColor
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentModel,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = StudioText
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (state.isOn) def.composeColor else StudioBorder)
                                )
                            }
                            val activeParams = def.getParamsForModel(state.selectedModelIndex)
                            Text(
                                text = "${def.models.size} model · " + activeParams.joinToString(", "),
                                fontSize = 11.sp,
                                color = StudioTextMuted
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Switch Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (state.isOn) StudioAccent.copy(alpha = 0.2f) else StudioCardElevated)
                                .border(1.dp, if (state.isOn) StudioAccent else StudioBorder, RoundedCornerShape(16.dp))
                                .clickable { onToggleModule(index) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (state.isOn) "ON" else "OFF",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (state.isOn) StudioAccent else StudioTextMuted
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Edit knobs",
                            tint = StudioTextMuted,
                            modifier = Modifier.size(20.dp)
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
