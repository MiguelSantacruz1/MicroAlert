package com.microalert.app.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme

val MicroAlertColorScheme = ColorScheme(
    primary = Color(0xFF00E5FF),
    onPrimary = Color(0xFF00363D),
    primaryContainer = Color(0xFF004F58),
    onPrimaryContainer = Color(0xFF97F0FF),
    secondary = Color(0xFFFFB74D),
    onSecondary = Color(0xFF452B00),
    secondaryContainer = Color(0xFF633F00),
    onSecondaryContainer = Color(0xFFFFDDB3),
    background = Color(0xFF101418),
    onBackground = Color(0xFFE1E2E6),
    surfaceContainer = Color(0xFF1C2024),
    onSurface = Color(0xFFE1E2E6),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005)
)

@Composable
fun MicroAlertTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MicroAlertColorScheme,
        content = content
    )
}
