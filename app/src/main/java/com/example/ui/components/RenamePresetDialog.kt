package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.theme.StudioTextMuted

@Composable
fun RenamePresetDialog(
    initialName: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var nameInput by remember { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = StudioCardElevated,
        shape = RoundedCornerShape(16.dp),
        title = {
            Text(
                text = "Ubah Nama Preset",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = StudioText
            )
        },
        text = {
            Column {
                Text(
                    text = "Maksimal 16 karakter (standar hardware GP-180)",
                    fontSize = 11.sp,
                    color = StudioTextMuted,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { if (it.length <= 16) nameInput = it },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("rename_preset_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StudioAccent,
                        unfocusedBorderColor = StudioBorder,
                        focusedTextColor = StudioText,
                        unfocusedTextColor = StudioText,
                        focusedContainerColor = StudioBg,
                        unfocusedContainerColor = StudioBg
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Text(
                    text = "${nameInput.length}/16 karakter",
                    fontSize = 10.sp,
                    color = StudioTextMuted,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(nameInput.trim())
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = StudioAccent,
                    contentColor = StudioBg
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Simpan", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = StudioTextMuted)
            }
        }
    )
}
