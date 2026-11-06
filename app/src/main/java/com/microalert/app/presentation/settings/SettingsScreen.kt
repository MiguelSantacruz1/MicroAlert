package com.microalert.app.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.microalert.app.domain.model.AppMode
import com.microalert.app.presentation.components.StatusChip

/**
 * Pantalla SETTINGS para ajustar modos, vibración, prueba de reacción y privacidad.
 */
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onToggleAppMode: () -> Unit,
    onToggleVibration: () -> Unit,
    onToggleReactionTest: () -> Unit,
    onCycleCooldown: () -> Unit = {},
    onClearAllData: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    ScalingLazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item {
            Text(
                text = "CONFIGURACIÓN",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            StatusChip(
                label = "Modo de Uso",
                statusText = uiState.appMode.title,
                statusColor = if (uiState.appMode == AppMode.REAL) Color(0xFF4CAF50) else Color(0xFFFF9800),
                onClick = onToggleAppMode
            )
        }

        item {
            StatusChip(
                label = "Vibración Alertas",
                statusText = if (uiState.isVibrationEnabled) "Activada" else "Desactivada",
                statusColor = if (uiState.isVibrationEnabled) Color(0xFF4CAF50) else Color(0xFFF44336),
                onClick = onToggleVibration
            )
        }

        item {
            StatusChip(
                label = "Enfriamiento Alerta",
                statusText = "${uiState.vibrationCooldownSeconds}s",
                statusColor = MaterialTheme.colorScheme.primary,
                onClick = onCycleCooldown
            )
        }

        item {
            StatusChip(
                label = "Prueba de Reacción",
                statusText = if (uiState.isReactionTestEnabled) "Cada ${uiState.reactionTestIntervalMinutes}m" else "Desactivada",
                statusColor = if (uiState.isReactionTestEnabled) MaterialTheme.colorScheme.primary else Color(0xFFFF9800),
                onClick = onToggleReactionTest
            )
        }

        item {
            StatusChip(
                label = "Ventana Temporal",
                statusText = "${uiState.windowSizeSeconds}s / ${uiState.windowStepSeconds}s",
                statusColor = MaterialTheme.colorScheme.onSurface,
                onClick = {}
            )
        }

        item {
            StatusChip(
                label = "Privacidad",
                statusText = "Borrar datos locales",
                statusColor = MaterialTheme.colorScheme.error,
                onClick = onClearAllData
            )
        }
    }
}
