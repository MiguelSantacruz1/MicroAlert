package com.microalert.app.presentation.monitoring

import androidx.lifecycle.ViewModel
import com.microalert.app.domain.model.RiskLevel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel para controlar las emisiones de métricas en vivo en la pantalla de Monitoreo.
 */
class MonitoringViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MonitoringUiState())
    val uiState: StateFlow<MonitoringUiState> = _uiState.asStateFlow()

    fun updateMetrics(
        bpm: Float,
        accMag: Float,
        gyroMag: Float,
        inactivitySec: Long,
        sessionSec: Long,
        riskLevel: RiskLevel
    ) {
        _uiState.update { current ->
            current.copy(
                heartRateBpm = bpm,
                accelerometerMagnitude = accMag,
                gyroscopeMagnitude = gyroMag,
                inactivityDurationSeconds = inactivitySec,
                sessionDurationSeconds = sessionSec,
                currentRiskLevel = riskLevel
            )
        }
    }
}
