package com.microalert.app.presentation.alert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text

/**
 * Pantalla ALERT de alto impacto visual y háptico para notificar somnolencia.
 */
@Composable
fun AlertScreen(
    uiState: AlertUiState,
    onAcknowledgeAlert: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(uiState.riskLevel.color.copy(alpha = 0.35f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🚨 ALERTA",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = uiState.riskLevel.color
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = uiState.riskLevel.label,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = uiState.alertMessage,
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(14.dp))
        Button(
            onClick = onAcknowledgeAlert,
            colors = ButtonDefaults.buttonColors(
                containerColor = uiState.riskLevel.color
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "RECONOCER",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
