package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EffectModuleDef
import com.example.data.model.ModuleState
import com.example.ui.theme.StudioAccent
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCard
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.StudioText
import com.example.ui.theme.StudioTextMuted

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ModuleEditorCard(
    moduleDef: EffectModuleDef,
    moduleState: ModuleState,
    onTogglePower: () -> Unit,
    onStepModel: (Int) -> Unit,
    onSelectModel: (Int) -> Unit,
    onKnobChange: (paramIndex: Int, value: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var showModelSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = StudioCard),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(StudioBorder))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header Row: Module ID, Power Switch, Model Chooser
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Category Indicator Dot
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(moduleDef.composeColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = moduleDef.id,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = StudioText
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Power Toggle Button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (moduleState.isOn) StudioAccent.copy(alpha = 0.2f) else StudioCardElevated)
                            .border(
                                1.dp,
                                if (moduleState.isOn) StudioAccent else StudioBorder,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { onTogglePower() }
                            .testTag("power_toggle_${moduleDef.id}")
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PowerSettingsNew,
                                contentDescription = if (moduleState.isOn) "Bypass module" else "Turn on module",
                                tint = if (moduleState.isOn) StudioAccent else StudioTextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (moduleState.isOn) "ON" else "OFF",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (moduleState.isOn) StudioAccent else StudioTextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Model Selector Pill with Stepper
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(StudioCardElevated)
                            .border(1.dp, StudioBorder, RoundedCornerShape(16.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        IconButton(
                            onClick = { onStepModel(-1) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Model sebelumnya",
                                tint = StudioTextMuted
                            )
                        }

                        val currentModelName = moduleDef.models.getOrNull(moduleState.selectedModelIndex) ?: "Model"
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { showModelSheet = true }
                                .padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = currentModelName,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = StudioText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.width(110.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Buka daftar model",
                                tint = StudioTextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = { onStepModel(1) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Model berikutnya",
                                tint = StudioTextMuted
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Model description banner (e.g. for Gate 1 vs Gate 2)
            val modelDesc = moduleDef.getModelDescription(moduleState.selectedModelIndex)
            if (modelDesc != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(StudioCardElevated)
                        .border(1.dp, StudioBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = moduleDef.composeColor,
                            modifier = Modifier
                                .size(14.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = modelDesc,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = StudioTextMuted
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Knobs Flow Row (dynamically adjusted per model, e.g. Gate 1 has 1 knob, Gate 2 has 3 knobs)
            val activeParams = moduleDef.getParamsForModel(moduleState.selectedModelIndex)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                activeParams.forEachIndexed { paramIndex, paramName ->
                    val knobValue = moduleState.values.getOrNull(paramIndex) ?: 50f
                    RotaryKnob(
                        label = paramName,
                        value = knobValue,
                        onValueChange = { newVal ->
                            onKnobChange(paramIndex, newVal)
                        },
                        accentColor = if (moduleState.isOn) moduleDef.composeColor else StudioTextMuted,
                        enabled = moduleState.isOn
                    )
                }
            }
        }
    }

    // Modal Bottom Sheet for selecting models
    if (showModelSheet) {
        ModalBottomSheet(
            onDismissRequest = { showModelSheet = false },
            sheetState = sheetState,
            containerColor = StudioCardElevated
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "Pilih Model ${moduleDef.id}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudioText
                )
                Text(
                    text = "${moduleDef.models.size} model emulasi tersedia untuk ${moduleDef.id}",
                    fontSize = 12.sp,
                    color = StudioTextMuted,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                ) {
                    itemsIndexed(moduleDef.models) { index, modelName ->
                        val isCurrent = index == moduleState.selectedModelIndex
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isCurrent) StudioAccent.copy(alpha = 0.15f) else androidx.compose.ui.graphics.Color.Transparent)
                                .clickable {
                                    onSelectModel(index)
                                    showModelSheet = false
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = modelName,
                                    fontSize = 14.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCurrent) StudioAccent else StudioText
                                )
                                val desc = moduleDef.getModelDescription(index)
                                if (desc != null) {
                                    Text(
                                        text = desc,
                                        fontSize = 11.sp,
                                        lineHeight = 14.sp,
                                        color = StudioTextMuted,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                            if (isCurrent) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(StudioAccent)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
