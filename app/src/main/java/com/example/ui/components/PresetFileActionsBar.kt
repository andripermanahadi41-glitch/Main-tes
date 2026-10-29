package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.StudioAccent
import com.example.ui.theme.StudioBg
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.StudioText

@Composable
fun PresetFileActionsBar(
    onOpenFile: () -> Unit,
    onSaveFile: () -> Unit,
    onShareFile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Open file button
        Button(
            onClick = onOpenFile,
            modifier = Modifier
                .weight(1f)
                .testTag("open_file_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = StudioCardElevated,
                contentColor = StudioText
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FileOpen,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Buka file",
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
        }

        // Save to phone (.prst) button
        Button(
            onClick = onSaveFile,
            modifier = Modifier
                .weight(1.3f)
                .testTag("save_file_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = StudioAccent,
                contentColor = StudioBg
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FileDownload,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Simpan .prst",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }

        // Quick Share
        Button(
            onClick = onShareFile,
            modifier = Modifier
                .testTag("share_file_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = StudioCardElevated,
                contentColor = StudioText
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Bagikan preset",
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
