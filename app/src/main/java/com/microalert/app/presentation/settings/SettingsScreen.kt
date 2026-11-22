package com.microalert.app.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun SettingsScreen(
    uiState: SettingsUiState,
    onToggleVibration: () -> Unit,
    onCycleCooldown: () -> Unit,
    onOpenReactionTest: () -> Unit,
    onClearAllData: () -> Unit,
    modifier: Modifier = Modifier
) {
    var confirmClear by remember { mutableStateOf(false) }

    NeonBackdrop(accent = Neon.Magenta, modifier = modifier) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            item { NeonTitle("AJUSTES", accent = Neon.Magenta) }

            item {
                NeonRow(
                    label = "Vibración",
                    value = if (uiState.isVibrationEnabled) "ON" else "OFF",
                    accent = if (uiState.isVibrationEnabled) Neon.Lime else Neon.Red,
                    onClick = onToggleVibration
                )
            }

            item {
                NeonRow(
                    label = "Entre alertas",
                    value = "${uiState.vibrationCooldownSeconds}s",
                    accent = Neon.Cyan,
                    onClick = onCycleCooldown
                )
            }

            item {
                NeonRow(
                    label = "Prueba de reacción",
                    value = "ABRIR",
                    accent = Neon.Violet,
                    onClick = onOpenReactionTest
                )
            }

            item {
                Spacer(Modifier.height(4.dp))
                GhostButton(
                    text = if (confirmClear) "¿SEGURO? TOCA DE NUEVO" else "BORRAR DATOS",
                    onClick = {
                        if (confirmClear) {
                            confirmClear = false
                            onClearAllData()
                        } else {
                            confirmClear = true
                        }
                    },
                    accent = Neon.Red
                )
            }
        }
    }
}
