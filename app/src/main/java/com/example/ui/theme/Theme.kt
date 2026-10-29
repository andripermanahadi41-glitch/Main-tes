package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = StudioAccent,
    onPrimary = StudioBg,
    primaryContainer = StudioCardElevated,
    onPrimaryContainer = StudioText,
    secondary = StudioAccent,
    onSecondary = StudioBg,
    background = StudioBg,
    onBackground = StudioText,
    surface = StudioCard,
    onSurface = StudioText,
    surfaceVariant = StudioCardElevated,
    onSurfaceVariant = StudioTextMuted,
    outline = StudioBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Default to dark studio guitar gear theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
