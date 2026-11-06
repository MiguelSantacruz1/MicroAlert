package com.microalert.app.presentation.alert

import com.microalert.app.domain.model.RiskLevel

/**
 * Estado UI para la pantalla de Alerta crítica o de alto riesgo.
 */
data class AlertUiState(
    val alertId: String = "",
    val riskLevel: RiskLevel = RiskLevel.CRITICAL,
    val alertMessage: String = "¡ALERTA DE SOMNOLENCIA! Se detectó un patrón de riesgo elevado.",
    val probability: Float = 0.95f,
    val isAcknowledged: Boolean = false,
    val timestampMs: Long = System.currentTimeMillis()
)
