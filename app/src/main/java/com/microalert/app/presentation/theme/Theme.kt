package com.microalert.app.presentation.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme

fun microAlertColorScheme() = ColorScheme(
    primary = Neon.Cyan,
    onPrimary = Neon.Void,
    primaryContainer = Neon.Panel,
    onPrimaryContainer = Neon.TextHi,
    secondary = Neon.Magenta,
    onSecondary = Neon.Void,
    secondaryContainer = Neon.Panel,
    onSecondaryContainer = Neon.TextHi,
    background = Neon.Void,
    onBackground = Neon.TextHi,
    surfaceContainer = Neon.Panel,
    onSurface = Neon.TextHi,
    onSurfaceVariant = Neon.TextLo,
    error = Neon.Red,
    onError = Neon.Void
)

@Composable
fun MicroAlertTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = microAlertColorScheme(),
        content = content
    )
}
