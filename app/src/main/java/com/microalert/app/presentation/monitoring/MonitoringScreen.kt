package com.microalert.app.presentation.monitoring

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.microalert.app.presentation.components.RiskCard
import com.microalert.app.presentation.components.StatusChip

/**
 * Pantalla de Monitoreo en Tiempo Real (MONITORING).
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
                text = "MONITOREO EN VIVO",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
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
