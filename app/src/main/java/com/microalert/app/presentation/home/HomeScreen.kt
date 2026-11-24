package com.microalert.app.presentation.home

import androidx.compose.foundation.layout.Arrangement
import com.microalert.app.presentation.theme.tr
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material3.Text
import com.microalert.app.domain.model.AppMode
import com.microalert.app.presentation.theme.GlyphButton
import com.microalert.app.presentation.theme.Neon
import com.microalert.app.presentation.theme.NeonBackdrop
import com.microalert.app.presentation.theme.NeonGlyph
import com.microalert.app.presentation.theme.NeonRow
import com.microalert.app.presentation.theme.NeonTitle
import com.microalert.app.presentation.theme.PowerOrb

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
    val isDemo = uiState.appMode == AppMode.DEMO
    NeonBackdrop(accent = if (uiState.isMonitoringActive) Neon.Lime else Neon.Violet, modifier = modifier) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { NeonTitle("MICROALERT") }

            item {
                Spacer(Modifier.height(2.dp))
                PowerOrb(
                    label = if (uiState.isMonitoringActive) tr("EN VIVO", "LIVE") else tr("INICIAR", "START"),
                    sublabel = if (uiState.isMonitoringActive) tr("toca para ver", "tap to view") else tr("monitoreo", "monitoring"),
                    live = uiState.isMonitoringActive,
                    onClick = onStartMonitoringClick
                )
            }

            item {
                NeonRow(
                    label = tr("Modo", "Mode"),
                    value = if (isDemo) "DEMO" else "REAL",
                    accent = if (isDemo) Neon.Amber else Neon.Lime,
                    onClick = onToggleAppMode
                )
            }

            item {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    GlyphButton(
                        glyph = NeonGlyph.PULSE,
                        label = "${uiState.activeSensorsCount}/${uiState.totalSensorsCount}",
                        accent = if (uiState.activeSensorsCount >= 3) Neon.Cyan else Neon.Amber,
                        onClick = onNavigateToSensorStatus
                    )
                    GlyphButton(NeonGlyph.CLOCK, tr("Historial", "History"), Neon.Violet, onNavigateToHistory)
                    GlyphButton(NeonGlyph.SLIDERS, tr("Ajustes", "Settings"), Neon.Magenta, onNavigateToSettings)
                }
            }

            item {
                Text(
                    text = tr("Prototipo, no es un dispositivo médico", "Prototype, not a medical device"),
                    color = Neon.TextLo,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}
