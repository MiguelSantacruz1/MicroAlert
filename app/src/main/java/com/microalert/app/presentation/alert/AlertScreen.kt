package com.microalert.app.presentation.alert

import androidx.compose.animation.core.RepeatMode
import com.microalert.app.presentation.theme.localAlertText
import com.microalert.app.presentation.theme.localLabel
import com.microalert.app.presentation.theme.tr
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.microalert.app.domain.model.RiskLevel
import com.microalert.app.presentation.theme.ExpandingRings
import com.microalert.app.presentation.theme.GlowText
import com.microalert.app.presentation.theme.Neon
import com.microalert.app.presentation.theme.NeonBackdrop
import com.microalert.app.presentation.theme.NeonButton
import com.microalert.app.presentation.theme.WarningGlyph
import com.microalert.app.presentation.theme.neon

@Composable
fun AlertScreen(
    uiState: AlertUiState,
    onAcknowledgeAlert: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = uiState.riskLevel.neon
    val critical = uiState.riskLevel == RiskLevel.CRITICAL
    val transition = rememberInfiniteTransition(label = "alert")
    val flash by transition.animateFloat(
        0.6f, 1f,
        infiniteRepeatable(tween(if (critical) 350 else 800), RepeatMode.Reverse),
        label = "flash"
    )

    NeonBackdrop(accent = color, modifier = modifier) {
        ExpandingRings(color = color, fast = critical)
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                Box(Modifier.alpha(flash)) { WarningGlyph(color = color) }
            }
            item {
                GlowText(
                    text = uiState.riskLevel.localLabel,
                    color = Neon.TextHi,
                    glow = color,
                    fontSize = 26.sp,
                    letterSpacing = 3.sp,
                    fontWeight = FontWeight.Black
                )
            }
            item {
                Text(
                    text = uiState.riskLevel.localAlertText,
                    color = Neon.TextHi,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Spacer(Modifier.height(4.dp))
                NeonButton(
                    text = tr("ENTENDIDO", "GOT IT"),
                    onClick = onAcknowledgeAlert,
                    colors = listOf(color, Neon.Magenta)
                )
            }
        }
    }
}
