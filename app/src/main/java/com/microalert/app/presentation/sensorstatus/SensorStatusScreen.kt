package com.microalert.app.presentation.sensorstatus

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
import com.microalert.app.presentation.components.StatusChip

@Composable
fun SensorStatusScreen(
    uiState: SensorStatusUiState,
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
                text = "ESTADO SENSORES",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            StatusChip(
                label = "Frecuencia Cardíaca",
                statusText = if (uiState.isHeartRateAvailable) "Disponible" else "No disponible",
                statusColor = if (uiState.isHeartRateAvailable) Color(0xFF4CAF50) else Color(0xFFF44336),
                onClick = {}
            )
        }

        item {
            StatusChip(
                label = "Acelerómetro",
                statusText = if (uiState.isAccelerometerAvailable) "Disponible" else "No disponible",
                statusColor = if (uiState.isAccelerometerAvailable) Color(0xFF4CAF50) else Color(0xFFF44336),
                onClick = {}
            )
        }

        item {
            StatusChip(
                label = "Giroscopio",
                statusText = if (uiState.isGyroscopeAvailable) "Disponible" else "No disponible",
                statusColor = if (uiState.isGyroscopeAvailable) Color(0xFF4CAF50) else Color(0xFFF44336),
                onClick = {}
            )
        }

        item {
            StatusChip(
                label = "Variabilidad HRV",
                statusText = if (uiState.isHrvAvailable) "Disponible" else "No expuesto",
                statusColor = if (uiState.isHrvAvailable) Color(0xFF4CAF50) else Color(0xFFFF9800),
                onClick = {}
            )
        }

        item {
            StatusChip(
                label = "Permiso Sensores",
                statusText = if (uiState.hasBodySensorsPermission) "Concedido" else "Pendiente",
                statusColor = if (uiState.hasBodySensorsPermission) Color(0xFF4CAF50) else Color(0xFFF44336),
                onClick = {}
            )
        }

        item {
            StatusChip(
                label = "Permiso Actividad",
                statusText = if (uiState.hasActivityRecognitionPermission) "Concedido" else "Pendiente",
                statusColor = if (uiState.hasActivityRecognitionPermission) Color(0xFF4CAF50) else Color(0xFFFF9800),
                onClick = {}
            )
        }
    }
}
