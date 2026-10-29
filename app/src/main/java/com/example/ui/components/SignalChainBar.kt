package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GP180_MODULE_DEFS
import com.example.data.model.ModuleState
import com.example.ui.theme.StudioAccent
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCard
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.StudioTextMuted

@Composable
fun SignalChainBar(
    modules: List<ModuleState>,
    selectedIndex: Int,
    onSelectModule: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        GP180_MODULE_DEFS.forEachIndexed { index, def ->
            val state = modules.getOrNull(index) ?: return@forEachIndexed
            val isSelected = index == selectedIndex
            val isOn = state.isOn
            val modelName = def.models.getOrNull(state.selectedModelIndex).orEmpty()

            Box(
                modifier = Modifier
                    .width(66.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isOn) StudioCardElevated else StudioCard)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) StudioAccent else StudioBorder.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .clickable { onSelectModule(index) }
                    .testTag("module_item_${def.id}")
                    .padding(vertical = 8.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                // Top-right LED indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 4.dp, top = 2.dp)
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (isOn) def.composeColor else StudioBorder)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = def.id,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isOn) def.composeColor else StudioTextMuted
                    )

                    Text(
                        text = modelName.take(8),
                        fontSize = 10.sp,
                        color = StudioTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
