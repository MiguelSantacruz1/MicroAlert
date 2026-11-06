package com.microalert.app.presentation.history

import com.microalert.app.domain.model.RiskLevel

/**
 * Modelo de una sesión registrada en el historial.
 */
data class SessionHistoryItem(
    val id: Long,
    val dateFormatted: String,
    val durationFormatted: String,
    val maxRiskLevel: RiskLevel,
    val alertCount: Int
)

/**
 * Estado UI de la pantalla de Historial de Sesiones.
 */
data class HistoryUiState(
    val sessions: List<SessionHistoryItem> = listOf(
        SessionHistoryItem(1, "Hoy, 10:15 AM", "45 min", RiskLevel.NORMAL, 0),
        SessionHistoryItem(2, "Ayer, 03:20 PM", "60 min", RiskLevel.PRECAUTION, 1),
        SessionHistoryItem(3, "28 Feb, 11:00 PM", "90 min", RiskLevel.HIGH, 3)
    ),
    val isLoading: Boolean = false
)
