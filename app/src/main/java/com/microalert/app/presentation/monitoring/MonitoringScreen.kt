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
 * Pantalla de Monitoreo en Tiempo Real con métricas procesadas de ventana (BPM, HRV SDNN/RMSSD, inactividad).
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
                label = "HRV (SDNN / RMSSD)",
                statusText = if (uiState.hrvAvailable && uiState.hrvSdnnMs != null)
                    "${uiState.hrvSdnnMs.toInt()}ms / ${uiState.hrvRmssdMs?.toInt()}ms"
                else "N/D (Sin datos IBI)",
                statusColor = if (uiState.hrvAvailable) MaterialTheme.colorScheme.primary else Color(0xFFFF9800),
                onClick = {}
            )
        }

        item {
            StatusChip(
                label = "Inactividad Procesada",
                statusText = "${uiState.inactivityDurationSeconds}s",
                statusColor = MaterialTheme.colorScheme.secondary,
                onClick = {}
            )
        }

        item {
            StatusChip(
                label = "Ventanas Procesadas",
                statusText = "#${uiState.windowCountProcessed}",
                statusColor = MaterialTheme.colorScheme.onSurface,
                onClick = {}
            )
        }
    }
}
