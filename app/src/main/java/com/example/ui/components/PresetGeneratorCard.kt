package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StudioAccent
import com.example.ui.theme.StudioBg
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCard
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.StudioText
import com.example.ui.theme.StudioTextMuted

@Composable
fun PresetGeneratorCard(
    band: String,
    onBandChange: (String) -> Unit,
    song: String,
    onSongChange: (String) -> Unit,
    link: String,
    onLinkChange: (String) -> Unit,
    instrument: String, // "gitar" or "bass"
    onInstrumentChange: (String) -> Unit,
    isGenerating: Boolean,
    onGenerate: () -> Unit,
    onSelectSample: (band: String, song: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cari preset lagu",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = StudioText
                )

                // Instrument Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Untuk:",
                        fontSize = 11.sp,
                        color = StudioTextMuted
                    )

                    listOf("gitar" to "Gitar", "bass" to "Bass").forEach { (key, label) ->
                        val isSelected = instrument.lowercase() == key
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) StudioAccent else StudioCardElevated)
                                .clickable { onInstrumentChange(key) }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) StudioBg else StudioText
                            )
                        }
                    }
                }
            }

            Text(
                text = "Isi nama artis dan judul lagu. AI akan menganalisis & meracik rantai modul GP180 yang akurat.",
                fontSize = 11.sp,
                color = StudioTextMuted,
                modifier = Modifier.padding(top = 2.dp, bottom = 8.dp)
            )

            // Input fields: Band and Song title side-by-side or stacked
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = band,
                    onValueChange = onBandChange,
                    placeholder = { Text("Nama band / artis", fontSize = 12.sp, color = StudioTextMuted) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("band_input"),
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

                OutlinedTextField(
                    value = song,
                    onValueChange = onSongChange,
                    placeholder = { Text("Judul lagu", fontSize = 12.sp, color = StudioTextMuted) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("song_input"),
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
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Samples Horizontal Scroll
            val samplesScroll = rememberScrollState()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(samplesScroll),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val samples = listOf(
                    "Metallica" to "Master of Puppets",
                    "Pink Floyd" to "Comfortably Numb",
                    "Red Hot Chili Peppers" to "Under the Bridge",
                    "Queen" to "Another One Bites the Dust",
                    "Nirvana" to "Smells Like Teen Spirit",
                    "Stevie Ray Vaughan" to "Pride and Joy"
                )

                samples.forEach { (sampleBand, sampleSong) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(StudioCardElevated)
                            .border(1.dp, StudioBorder, RoundedCornerShape(12.dp))
                            .clickable { onSelectSample(sampleBand, sampleSong) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$sampleBand - $sampleSong",
                            fontSize = 11.sp,
                            color = StudioText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // External Search Helper Links
            val searchQuery = "$band $song".trim()
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Dengarkan di:",
                    fontSize = 10.sp,
                    color = StudioTextMuted
                )

                val openUrl = { url: String ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(StudioCardElevated)
                        .clickable {
                            openUrl("https://www.youtube.com/results?search_query=" + Uri.encode(searchQuery))
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("YouTube ↗", fontSize = 10.sp, color = StudioAccent)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(StudioCardElevated)
                        .clickable {
                            openUrl("https://open.spotify.com/search/" + Uri.encode(searchQuery))
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("Spotify ↗", fontSize = 10.sp, color = StudioAccent)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(StudioCardElevated)
                        .clickable {
                            openUrl("https://soundcloud.com/search?q=" + Uri.encode(searchQuery))
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("SoundCloud ↗", fontSize = 10.sp, color = StudioAccent)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Generate Button
            Button(
                onClick = onGenerate,
                enabled = !isGenerating,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("generate_presets_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = StudioAccent,
                    contentColor = StudioBg
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = StudioBg,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Menyusun Preset...",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Generate Presets",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
