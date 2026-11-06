package com.microalert.app.presentation.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.microalert.app.presentation.components.StatusChip

/**
 * Pantalla SESSION HISTORY de sesiones de monitoreo registradas localmente.
 */
@Composable
fun SessionHistoryScreen(
    uiState: HistoryUiState,
    onClearHistoryClick: () -> Unit,
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
                text = "HISTORIAL",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (uiState.sessions.isEmpty()) {
            item {
                Text(
                    text = "No hay sesiones registradas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(uiState.sessions) { session ->
                StatusChip(
                    label = session.dateFormatted,
                    statusText = "${session.durationFormatted} | ${session.maxRiskLevel.label}",
                    statusColor = session.maxRiskLevel.color,
                    onClick = {}
                )
            }

            item {
                StatusChip(
                    label = "Privacidad",
                    statusText = "Borrar todo el historial",
                    statusColor = MaterialTheme.colorScheme.error,
                    onClick = onClearHistoryClick
                )
            }
        }
    }
}
