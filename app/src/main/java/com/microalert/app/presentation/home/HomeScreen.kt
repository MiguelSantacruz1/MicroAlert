package com.microalert.app.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.microalert.app.domain.model.AppMode
import com.microalert.app.presentation.components.StatusChip

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onStartMonitoringClick: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSensorStatus: () -> Unit,
    onToggleAppMode: () -> Unit,
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
                text = "MICROALERT",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            StatusChip(
                label = "Modo:",
                statusText = uiState.appMode.title,
                statusColor = if (uiState.appMode == AppMode.REAL) Color(0xFF4CAF50) else Color(0xFFFF9800),
                onClick = onToggleAppMode
            )
        }

        item {
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = onStartMonitoringClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (uiState.isMonitoringActive) Color(0xFFF44336) else MaterialTheme.colorScheme.primary
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (uiState.isMonitoringActive) "Detener Monitoreo" else "Iniciar Monitoreo",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        item {
            StatusChip(
                label = "Estado Sensores",
                statusText = "${uiState.activeSensorsCount}/${uiState.totalSensorsCount} OK",
                statusColor = MaterialTheme.colorScheme.primary,
                onClick = onNavigateToSensorStatus
            )
        }

        item {
            StatusChip(
                label = "Historial",
                statusText = "Ver todo",
                statusColor = MaterialTheme.colorScheme.onSurface,
                onClick = onNavigateToHistory
            )
        }

        item {
            StatusChip(
                label = "Configuración",
                statusText = "Ajustes",
                statusColor = MaterialTheme.colorScheme.onSurface,
                onClick = onNavigateToSettings
            )
        }
    }
}
