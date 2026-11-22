package com.microalert.app.presentation.sensorstatus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import com.microalert.app.presentation.theme.GhostButton
import com.microalert.app.presentation.theme.Neon
import com.microalert.app.presentation.theme.NeonBackdrop
import com.microalert.app.presentation.theme.NeonRow
import com.microalert.app.presentation.theme.NeonTitle

@Composable
fun SensorStatusScreen(
    uiState: SensorStatusUiState,
    onRequestPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val permissionsOk = uiState.hasBodySensorsPermission && uiState.hasActivityRecognitionPermission

    NeonBackdrop(accent = Neon.Cyan, modifier = modifier) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            item { NeonTitle("SENSORES") }

            item { SensorRow("Pulso", uiState.isHeartRateAvailable) }
            item { SensorRow("Acelerómetro", uiState.isAccelerometerAvailable) }
            item { SensorRow("Giroscopio", uiState.isGyroscopeAvailable) }
            item {
                NeonRow(
                    label = "HRV",
                    value = if (uiState.isHrvAvailable) "OK" else "No expuesto",
                    accent = if (uiState.isHrvAvailable) Neon.Lime else Neon.Amber
                )
            }
            item {
                NeonRow(
                    label = "Permisos",
                    value = if (permissionsOk) "OK" else "Faltan",
                    accent = if (permissionsOk) Neon.Lime else Neon.Red
                )
            }

            if (!permissionsOk) {
                item {
                    Spacer(Modifier.height(2.dp))
                    GhostButton(text = "CONCEDER", onClick = onRequestPermissions, accent = Neon.Cyan)
                }
            }
        }
    }
}

@Composable
private fun SensorRow(label: String, available: Boolean) {
    NeonRow(
        label = label,
        value = if (available) "OK" else "No disponible",
        accent = if (available) Neon.Lime else Neon.Red
    )
}
