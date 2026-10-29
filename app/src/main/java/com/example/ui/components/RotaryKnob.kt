package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StudioAccent
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.StudioText
import com.example.ui.theme.StudioTextMuted
import kotlin.math.roundToInt

@Composable
fun RotaryKnob(
    label: String,
    value: Float, // 0f .. 100f
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = StudioAccent,
    enabled: Boolean = true
) {
    var dragAccumulator by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = modifier
            .testTag("knob_$label")
            .padding(horizontal = 2.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .pointerInput(enabled) {
                    if (!enabled) return@pointerInput
                    detectDragGestures(
                        onDragStart = { dragAccumulator = 0f },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            // Dragging up increases value; dragging right also increases
                            val delta = (-dragAmount.y + dragAmount.x * 0.4f) * 0.7f
                            val newValue = (value + delta).coerceIn(0f, 100f)
                            if (newValue != value) {
                                onValueChange(newValue)
                            }
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasSize = size.minDimension
                val strokeWidth = canvasSize * 0.09f
                val padding = strokeWidth / 2f + 4.dp.toPx()
                val arcSize = Size(canvasSize - padding * 2, canvasSize - padding * 2)
                val topLeft = Offset(padding, padding)

                val center = Offset(size.width / 2f, size.height / 2f)
                val innerRadius = (canvasSize / 2f) - strokeWidth * 1.8f

                // Inner knob metal body
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            StudioCardElevated,
                            Color(0xFF141518)
                        ),
                        center = center,
                        radius = innerRadius
                    ),
                    radius = innerRadius,
                    center = center
                )

                // Inner rim border
                drawCircle(
                    color = StudioBorder,
                    radius = innerRadius,
                    center = center,
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // Background Track Arc (270 degrees total)
                val startAngle = 135f
                val totalSweep = 270f
                drawArc(
                    color = StudioBorder.copy(alpha = 0.8f),
                    startAngle = startAngle,
                    sweepAngle = totalSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Active Progress Arc
                val activeSweep = (value / 100f) * totalSweep
                if (activeSweep > 0.5f) {
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(accentColor.copy(alpha = 0.6f), accentColor),
                            center = center
                        ),
                        startAngle = startAngle,
                        sweepAngle = activeSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                // Pointer needle indicator inside knob
                val pointerAngle = -135f + (value / 100f) * 270f
                rotate(pointerAngle, center) {
                    drawLine(
                        color = if (enabled) StudioText else StudioTextMuted,
                        start = center,
                        end = Offset(center.x, center.y - innerRadius + 2.dp.toPx()),
                        strokeWidth = 2.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    // Glow tip
                    drawCircle(
                        color = accentColor,
                        radius = 2.dp.toPx(),
                        center = Offset(center.x, center.y - innerRadius + 3.dp.toPx())
                    )
                }
            }
        }

        // Parameter Name
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = StudioTextMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 2.dp)
        )

        // Percentage Value
        Text(
            text = "${value.roundToInt()}%",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = if (enabled) accentColor else StudioTextMuted,
            textAlign = TextAlign.Center
        )
    }
}
