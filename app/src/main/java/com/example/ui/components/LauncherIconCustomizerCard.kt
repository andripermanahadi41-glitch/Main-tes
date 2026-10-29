package com.example.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StudioAccent
import com.example.ui.theme.StudioBg
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCard
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.StudioText
import com.example.ui.theme.StudioTextMuted

@Composable
fun LauncherIconCustomizerCard(
    customBitmap: Bitmap?,
    customIconInfo: String?,
    onUploadIcon: (Bitmap, String) -> Unit,
    onResetIcon: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }
    var selectedShapeIndex by remember { mutableIntStateOf(0) } // 0: Squircle, 1: Circle, 2: Rounded Square

    // Modern Zero-permission Android Photo Picker for picking PNG images
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            try {
                // Determine file name
                var fileName = "custom_icon.png"
                context.contentResolver.query(selectedUri, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1 && cursor.moveToFirst()) {
                        fileName = cursor.getString(nameIndex) ?: "custom_icon.png"
                    }
                }

                // Decode bitmap
                context.contentResolver.openInputStream(selectedUri)?.use { stream ->
                    val bitmap = BitmapFactory.decodeStream(stream)
                    if (bitmap != null) {
                        onUploadIcon(bitmap, fileName)
                    } else {
                        Toast.makeText(context, "Gagal memproses gambar. Pastikan format PNG valid.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Kesalahan saat membuka gambar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("launcher_icon_customizer_card"),
        colors = CardDefaults.cardColors(containerColor = StudioCard),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(StudioBorder))
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Header Row: Icon, Title, Expand/Collapse Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(StudioCardElevated)
                            .border(1.dp, StudioAccent.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (customBitmap != null) {
                            Image(
                                bitmap = customBitmap.asImageBitmap(),
                                contentDescription = "Ikon Kustom",
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.valeton_gp180_icon_1793262145330),
                                contentDescription = "Ikon Valeton GP-180",
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Kustomisasi Ikon Peluncur",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = StudioText
                            )
                            if (customBitmap != null) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Ikon kustom aktif",
                                    tint = StatusGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Text(
                            text = if (customBitmap != null) customIconInfo ?: "PNG Kustom Terpasang"
                            else "Unggah gambar PNG untuk mengubah logo aplikasi",
                            fontSize = 11.sp,
                            color = if (customBitmap != null) StudioAccent else StudioTextMuted
                        )
                    }
                }

                IconButton(
                    onClick = { isExpanded = !isExpanded },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (isExpanded) "Tutup" else "Buka",
                        tint = StudioTextMuted
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                ) {
                    Text(
                        text = "Pratinjau Layar Beranda (Homescreen Preview):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = StudioTextMuted,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Homescreen Preview Mockup
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(StudioCardElevated)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Shapes preview: Squircle, Circle, Rounded Square
                        val shapes = listOf("Pixel (Squircle)", "Bulat (Circle)", "OneUI (Square)")

                        shapes.forEachIndexed { index, shapeLabel ->
                            val isSelectedShape = index == selectedShapeIndex
                            val shapeModifier = when (index) {
                                0 -> Modifier.clip(RoundedCornerShape(16.dp))
                                1 -> Modifier.clip(CircleShape)
                                else -> Modifier.clip(RoundedCornerShape(10.dp))
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clickable { selectedShapeIndex = index }
                                    .padding(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .then(shapeModifier)
                                        .background(Color(0xFF0A0A0B))
                                        .border(
                                            if (isSelectedShape) 2.dp else 1.dp,
                                            if (isSelectedShape) StudioAccent else StudioBorder,
                                            when (index) {
                                                0 -> RoundedCornerShape(16.dp)
                                                1 -> CircleShape
                                                else -> RoundedCornerShape(10.dp)
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (customBitmap != null) {
                                        Image(
                                            bitmap = customBitmap.asImageBitmap(),
                                            contentDescription = "Pratinjau Ikon $shapeLabel",
                                            modifier = Modifier
                                                .size(46.dp)
                                                .then(shapeModifier),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Image(
                                            painter = painterResource(id = R.drawable.valeton_gp180_icon_1793262145330),
                                            contentDescription = "Pratinjau Ikon Valeton GP-180 $shapeLabel",
                                            modifier = Modifier
                                                .size(46.dp)
                                                .then(shapeModifier),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }

                                Text(
                                    text = "GP180 Tone",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = StudioText,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                Text(
                                    text = shapeLabel.split(" ")[0],
                                    fontSize = 9.sp,
                                    color = if (isSelectedShape) StudioAccent else StudioTextMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action Buttons: Upload PNG & Reset
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("upload_launcher_icon_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StudioAccent,
                                contentColor = StudioBg
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileUpload,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Unggah Ikon PNG",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        if (customBitmap != null) {
                            OutlinedButton(
                                onClick = onResetIcon,
                                modifier = Modifier.testTag("reset_launcher_icon_button"),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = StudioTextMuted
                                ),
                                shape = RoundedCornerShape(10.dp),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(StudioBorder)
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Reset",
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Tips: Pilih gambar PNG dengan rasio 1:1 (disarankan 512x512 piksel) dengan latar transparan atau solid agar tampak presisi di peluncur Android.",
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        color = StudioTextMuted
                    )
                }
            }
        }
    }
}
