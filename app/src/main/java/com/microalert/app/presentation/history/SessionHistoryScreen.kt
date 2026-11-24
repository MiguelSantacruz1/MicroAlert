package com.microalert.app.presentation.history

import androidx.compose.foundation.layout.Arrangement
import com.microalert.app.presentation.theme.localLabel
import com.microalert.app.presentation.theme.tr
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material3.Text
import com.microalert.app.presentation.theme.GhostButton
import com.microalert.app.presentation.theme.GlowDot
import com.microalert.app.presentation.theme.Neon
import com.microalert.app.presentation.theme.NeonBackdrop
import com.microalert.app.presentation.theme.NeonCard
import com.microalert.app.presentation.theme.NeonTitle
import com.microalert.app.presentation.theme.neon

@Composable
fun SessionHistoryScreen(
    uiState: HistoryUiState,
    onClearHistoryClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLoadSamples: () -> Unit = {}
) {
    var confirmClear by remember { mutableStateOf(false) }

    NeonBackdrop(accent = Neon.Violet, modifier = modifier) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            item { NeonTitle(tr("HISTORIAL", "HISTORY"), accent = Neon.Violet) }

            when {
                uiState.isLoading -> item {
                    Text(tr("Cargando...", "Loading..."), color = Neon.TextLo, fontSize = 11.sp)
                }

                uiState.sessions.isEmpty() -> {
                    item {
                        Text(
                            text = tr("Aún no hay sesiones. Inicia un monitoreo para verlas aquí.", "No sessions yet. Start monitoring to see them here."),
                            color = Neon.TextLo,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                    item {
                        GhostButton(text = tr("CARGAR EJEMPLOS", "LOAD SAMPLES"), onClick = onLoadSamples, accent = Neon.Cyan)
                    }
                }

                else -> {
                    items(uiState.sessions) { session ->
                        NeonCard(accent = session.maxRiskLevel.neon) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                GlowDot(session.maxRiskLevel.neon)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = session.dateFormatted,
                                    color = Neon.TextHi,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(Modifier.height(3.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(session.durationFormatted, color = Neon.TextLo, fontSize = 10.sp)
                                Text(
                                    text = session.maxRiskLevel.localLabel,
                                    color = session.maxRiskLevel.neon,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (session.alertCount > 0) {
                                Text(
                                    text = alertsLabel(session.alertCount),
                                    color = Neon.Amber,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    item {
                        Spacer(Modifier.height(4.dp))
                        GhostButton(
                            text = if (confirmClear) tr("¿SEGURO? TOCA DE NUEVO", "SURE? TAP AGAIN") else tr("BORRAR TODO", "CLEAR ALL"),
                            onClick = {
                                if (confirmClear) {
                                    confirmClear = false
                                    onClearHistoryClick()
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
    }
}

private fun alertsLabel(count: Int): String = if (count == 1) tr("1 alerta", "1 alert") else tr("$count alertas", "$count alerts")
