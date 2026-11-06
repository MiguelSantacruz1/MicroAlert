package com.microalert.app.presentation.reaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text

/**
 * Pantalla interactiva para la Prueba de Reacción Táctil y Visual en Wear OS.
 */
@Composable
fun ReactionTestScreen(
    uiState: ReactionTestUiState,
    onStartTest: () -> Unit,
    onUserTap: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (uiState.phase) {
        ReactionTestPhase.STIMULUS_ACTIVE -> Color(0xFF4CAF50) // Verde brillante para pulsar
        ReactionTestPhase.WAITING_FOR_STIMULUS -> Color(0xFFFF9800) // Naranja de espera
        else -> MaterialTheme.colorScheme.background
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .clickable {
                if (uiState.phase == ReactionTestPhase.STIMULUS_ACTIVE || uiState.phase == ReactionTestPhase.WAITING_FOR_STIMULUS) {
                    onUserTap()
                }
            }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "PRUEBA DE REACCIÓN",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (uiState.phase == ReactionTestPhase.STIMULUS_ACTIVE) Color.White else MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = uiState.message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = if (uiState.phase == ReactionTestPhase.STIMULUS_ACTIVE) Color.White else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            when (uiState.phase) {
                ReactionTestPhase.INSTRUCTIONS -> {
                    Button(
                        onClick = onStartTest,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "INICIAR PRUEBA")
                    }
                }
                ReactionTestPhase.COMPLETED -> {
                    Button(
                        onClick = onFinish,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "FINALIZAR")
                    }
                }
                else -> {}
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "⚠️ No realizar conduciendo",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
