package com.microalert.app.presentation.history

import com.microalert.app.domain.model.RiskLevel

data class SessionHistoryItem(
    val id: Long,
    val dateFormatted: String,
    val durationFormatted: String,
    val maxRiskLevel: RiskLevel,
    val alertCount: Int
)

data class HistoryUiState(
    val sessions: List<SessionHistoryItem> = emptyList(),
    val isLoading: Boolean = true
)
