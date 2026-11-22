package com.microalert.app.presentation.reaction

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Text
import com.microalert.app.presentation.theme.GlowText
import com.microalert.app.presentation.theme.Neon
import com.microalert.app.presentation.theme.NeonBackdrop
import com.microalert.app.presentation.theme.NeonButton

@Composable
fun ReactionTestScreen(
    uiState: ReactionTestUiState,
    onStartTest: () -> Unit,
    onUserTap: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val phase = uiState.phase
    val accent by animateColorAsState(
        when (phase) {
            ReactionTestPhase.STIMULUS_ACTIVE -> Neon.Lime
            ReactionTestPhase.WAITING_FOR_STIMULUS -> Neon.Amber
            ReactionTestPhase.COMPLETED -> Neon.Cyan
            ReactionTestPhase.INSTRUCTIONS -> Neon.Violet
        },
        tween(250),
        label = "accent"
    )
    val transition = rememberInfiniteTransition(label = "reaction")
    val pulse by transition.animateFloat(
        0.92f, 1.08f,
        infiniteRepeatable(
            tween(if (phase == ReactionTestPhase.STIMULUS_ACTIVE) 250 else 900, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val tappable = phase == ReactionTestPhase.STIMULUS_ACTIVE || phase == ReactionTestPhase.WAITING_FOR_STIMULUS
    val latency = uiState.lastLatencyMs

    NeonBackdrop(
        accent = accent,
        modifier = modifier.clickable(enabled = tappable) { onUserTap() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (tappable || phase == ReactionTestPhase.COMPLETED) {
                Box(
                    Modifier
                        .size(112.dp)
                        .drawBehind {
                            drawCircle(
                                Brush.radialGradient(listOf(accent.copy(alpha = 0.55f), Color.Transparent)),
                                radius = size.minDimension * 0.62f * pulse
                            )
                        }
                        .padding(14.dp)
                        .clip(CircleShape)
                        .background(Brush.radialGradient(listOf(accent.copy(alpha = 0.85f), accent.copy(alpha = 0.25f)))),
                    contentAlignment = Alignment.Center
                ) {
                    if (phase == ReactionTestPhase.COMPLETED && latency != null) {
                        GlowText(
                            text = "$latency",
                            color = Neon.Void,
                            glow = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            } else {
                GlowText(
                    text = "REACCIÓN",
                    color = accent,
                    fontSize = 18.sp,
                    letterSpacing = 3.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = if (phase == ReactionTestPhase.COMPLETED && latency != null) {
                    "milisegundos, ${rating(latency)}"
                } else {
                    uiState.message
                },
                color = Neon.TextHi,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(10.dp))

            when (phase) {
                ReactionTestPhase.INSTRUCTIONS -> NeonButton("COMENZAR", onStartTest)
                ReactionTestPhase.COMPLETED -> NeonButton("LISTO", onFinish)
                else -> Text("No lo uses mientras conduces", color = Neon.TextLo, fontSize = 9.sp)
            }
        }
    }
}

private fun rating(latencyMs: Long): String = when {
    latencyMs < 350 -> "alerta"
    latencyMs < 500 -> "normal"
    else -> "lento"
}
