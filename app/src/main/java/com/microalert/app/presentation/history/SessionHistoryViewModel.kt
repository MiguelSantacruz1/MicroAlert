package com.microalert.app.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microalert.app.domain.model.MonitoringSession
import com.microalert.app.domain.usecase.ClearHistoryUseCase
import com.microalert.app.domain.usecase.GetSessionHistoryUseCase
import com.microalert.app.utils.Formatters
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel reactivo para la pantalla de historial de sesiones.
 * Gestiona la carga de sesiones persistidas en Room y el borrado seguro para privacidad.
 */
class SessionHistoryViewModel(
    private val getSessionHistoryUseCase: GetSessionHistoryUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase
) : ViewModel() {

    val uiState: StateFlow<HistoryUiState> = getSessionHistoryUseCase()
        .map { sessions ->
            HistoryUiState(
                sessions = sessions.map { it.toUiItem() },
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HistoryUiState(sessions = emptyList(), isLoading = true)
        )

    fun clearHistory() {
        viewModelScope.launch {
            clearHistoryUseCase()
        }
    }

    private fun MonitoringSession.toUiItem(): SessionHistoryItem {
        return SessionHistoryItem(
            id = id,
            dateFormatted = Formatters.formatSessionDate(startTimestampMs),
            durationFormatted = Formatters.formatDuration(durationSeconds),
            maxRiskLevel = maxRiskLevel,
            alertCount = alertsTriggeredCount
        )
    }
}
