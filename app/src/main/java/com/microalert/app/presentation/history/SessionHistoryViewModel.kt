package com.microalert.app.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microalert.app.domain.model.MonitoringSession
import com.microalert.app.domain.usecase.ClearHistoryUseCase
import com.microalert.app.domain.usecase.GetSessionHistoryUseCase
import com.microalert.app.domain.usecase.SaveSessionUseCase
import com.microalert.app.domain.model.AppMode
import com.microalert.app.domain.model.RiskLevel
import com.microalert.app.utils.Formatters
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SessionHistoryViewModel(
    private val getSessionHistoryUseCase: GetSessionHistoryUseCase,
    private val clearHistoryUseCase: ClearHistoryUseCase,
    private val saveSessionUseCase: SaveSessionUseCase? = null
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

    fun loadSampleSessions() {
        val save = saveSessionUseCase ?: return
        val now = System.currentTimeMillis()
        val hour = 3_600_000L
        val samples = listOf(
            Triple(now - 2 * hour, 2700L, RiskLevel.NORMAL to 0),
            Triple(now - 26 * hour, 3600L, RiskLevel.PRECAUTION to 1),
            Triple(now - 50 * hour, 5400L, RiskLevel.HIGH to 3),
            Triple(now - 74 * hour, 1800L, RiskLevel.CRITICAL to 4)
        )
        viewModelScope.launch {
            samples.forEach { (start, seconds, risk) ->
                save(
                    MonitoringSession(
                        id = 0L,
                        startTimestampMs = start,
                        endTimestampMs = start + seconds * 1000L,
                        durationSeconds = seconds,
                        appMode = AppMode.DEMO,
                        maxRiskLevel = risk.first,
                        alertsTriggeredCount = risk.second,
                        avgHeartRateBpm = 68f,
                        processedWindowsCount = (seconds / 30).toInt()
                    )
                )
            }
        }
    }

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
