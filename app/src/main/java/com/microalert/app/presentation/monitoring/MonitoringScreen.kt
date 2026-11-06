package com.microalert.app.presentation.monitoring

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
import com.microalert.app.presentation.components.RiskCard
import com.microalert.app.presentation.components.StatusChip

/**
 * Pantalla de Monitoreo en Tiempo Real (MONITORING) con indicador de lecturas y calidad de señal.
 */
@Composable
fun MonitoringScreen(
    uiState: MonitoringUiState,
    onStopMonitoringClick: () -> Unit,
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
                text = "MONITOREO [${uiState.appMode.title}]",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (uiState.appMode == AppMode.REAL) Color(0xFF4CAF50) else Color(0xFFFF9800)
            )
        }

        item {
            RiskCard(riskLevel = uiState.currentRiskLevel)
        }

        item {
            StatusChip(
                label = "Frecuencia Cardíaca",
                statusText = "${uiState.heartRateBpm.toInt()} BPM",
                statusColor = MaterialTheme.colorScheme.primary,
                onClick = {}
            )
        }

        item {
            StatusChip(
                label = "Acelerómetro",
                statusText = String.format("%.2f m/s²", uiState.accelerometerMagnitude),
                statusColor = MaterialTheme.colorScheme.secondary,
                onClick = {}
            )
        }

        item {
            StatusChip(
                label = "Giroscopio",
                statusText = String.format("%.2f rad/s", uiState.gyroscopeMagnitude),
                statusColor = MaterialTheme.colorScheme.secondary,
                onClick = {}
            )
        }

        item {
            StatusChip(
                label = "Inactividad",
                statusText = "${uiState.inactivityDurationSeconds}s",
                statusColor = MaterialTheme.colorScheme.onSurface,
                onClick = {}
            )
        }

        item {
            StatusChip(
                label = "Duración Sesión",
                statusText = "${uiState.sessionDurationSeconds / 60}m ${uiState.sessionDurationSeconds % 60}s",
                statusColor = MaterialTheme.colorScheme.onSurface,
                onClick = {}
            )
        }
    }
}
