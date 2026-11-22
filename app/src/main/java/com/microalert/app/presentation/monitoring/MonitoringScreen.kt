package com.microalert.app.presentation.monitoring

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.material3.Text
import com.microalert.app.domain.model.AppMode
import com.microalert.app.sensors.DemoLevel
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.microalert.app.presentation.theme.GhostButton
import com.microalert.app.presentation.theme.GlowDot
import com.microalert.app.presentation.theme.GlowText
import com.microalert.app.presentation.theme.HeartbeatIcon
import com.microalert.app.presentation.theme.Neon
import com.microalert.app.presentation.theme.NeonBackdrop
import com.microalert.app.presentation.theme.RiskGauge
import com.microalert.app.presentation.theme.StatTile
import com.microalert.app.presentation.theme.neon
import com.microalert.app.utils.Formatters

@Composable
fun MonitoringScreen(
    uiState: MonitoringUiState,
    onStopMonitoringClick: () -> Unit,
    modifier: Modifier = Modifier,
    onDemoLevel: (DemoLevel) -> Unit = {}
) {
    val risk = uiState.currentRiskLevel
    val isDemo = uiState.appMode == AppMode.DEMO

    NeonBackdrop(accent = risk.neon, modifier = modifier) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { LiveTag(isDemo) }

            item {
                RiskGauge(level = risk, modifier = Modifier.size(172.dp)) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        HeartbeatIcon(
                            bpm = if (uiState.heartRateValid) uiState.heartRateBpm else 0f,
                            color = risk.neon
                        )
                        GlowText(
                            text = if (uiState.heartRateValid) "${uiState.heartRateBpm.toInt()}" else "--",
                            color = Neon.TextHi,
                            glow = risk.neon,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(text = "BPM", color = Neon.TextLo, fontSize = 9.sp, letterSpacing = 2.sp)
                    }
                }
            }

            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    GlowText(
                        text = risk.label,
                        color = risk.neon,
                        fontSize = 21.sp,
                        letterSpacing = 3.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = risk.description,
                        color = Neon.TextLo,
                        fontSize = 10.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(0.85f)
                    )
                }
            }

            if (isDemo) {
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("ESCENARIO DEMO", color = Neon.Amber, fontSize = 9.sp, letterSpacing = 2.sp)
                        Spacer(Modifier.height(4.dp))
                        DemoLevel.entries.chunked(2).forEachIndexed { index, row ->
                            if (index > 0) Spacer(Modifier.height(5.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                row.forEach { DemoPill(it, uiState.demoLevel, demoAccent(it), onDemoLevel) }
                            }
                        }
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    StatTile(
                        label = "SESIÓN",
                        value = Formatters.formatClock(uiState.sessionDurationSeconds),
                        accent = Neon.Cyan,
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        label = "QUIETO",
                        value = "${uiState.inactivityDurationSeconds}s",
                        accent = Neon.Violet,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    StatTile(
                        label = "HRV SDNN",
                        value = if (uiState.hrvAvailable && uiState.hrvSdnnMs != null) "${uiState.hrvSdnnMs.toInt()}ms" else "N/D",
                        accent = if (uiState.hrvAvailable) Neon.Lime else Neon.Amber,
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        label = "VENTANAS",
                        value = "${uiState.windowCountProcessed}",
                        accent = Neon.Magenta,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                GhostButton(
                    text = "DETENER",
                    onClick = onStopMonitoringClick,
                    accent = Neon.Red,
                    modifier = Modifier.fillMaxWidth(0.8f)
                )
            }
        }
    }
}

@Composable
private fun LiveTag(isDemo: Boolean) {
    val transition = rememberInfiniteTransition(label = "live")
    val blink by transition.animateFloat(
        0.25f, 1f,
        infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "blink"
    )
    val color = if (isDemo) Neon.Amber else Neon.Lime
    Row(verticalAlignment = Alignment.CenterVertically) {
        Row(Modifier.alpha(blink)) { GlowDot(color, 7.dp) }
        Spacer(Modifier.width(7.dp))
        Text(
            text = if (isDemo) "DEMO EN VIVO" else "SENSORES EN VIVO",
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
    }
}

@Composable
private fun DemoPill(
    level: DemoLevel,
    selected: DemoLevel,
    accent: Color,
    onClick: (DemoLevel) -> Unit
) {
    val shape = RoundedCornerShape(50)
    val on = level == selected
    Text(
        text = level.label,
        color = if (on) Neon.Void else accent,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(shape)
            .background(if (on) accent else accent.copy(alpha = 0.12f))
            .border(1.dp, accent, shape)
            .clickable { onClick(level) }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

private fun demoAccent(level: DemoLevel): Color = when (level) {
    DemoLevel.AUTO -> Neon.Cyan
    DemoLevel.NORMAL -> Neon.Lime
    DemoLevel.PRECAUTION -> Neon.Yellow
    DemoLevel.HIGH -> Neon.Amber
    DemoLevel.CRITICAL -> Neon.Red
}
