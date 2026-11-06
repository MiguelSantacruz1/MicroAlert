package com.microalert.app.presentation.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microalert.app.data.repository.SensorRepositoryImpl
import com.microalert.app.domain.model.AppMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel para controlar el monitoreo y recolectar las muestras en vivo y ventanas procesadas.
 */
class MonitoringViewModel(
    private val sensorRepository: SensorRepositoryImpl? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonitoringUiState())
    val uiState: StateFlow<MonitoringUiState> = _uiState.asStateFlow()

    init {
        sensorRepository?.let { repo ->
            viewModelScope.launch {
                repo.getSensorDataStream().collect { sample ->
                    _uiState.update { current ->
                        current.copy(
                            heartRateBpm = sample.heartRateBpm ?: current.heartRateBpm,
                            accelerometerMagnitude = sample.accelMagnitude,
                            gyroscopeMagnitude = sample.gyroMagnitude
                        )
                    }
                }
            }

            viewModelScope.launch {
                repo.getProcessedWindowStream().collect { window ->
                    _uiState.update { current ->
                        current.copy(
                            inactivityDurationSeconds = window.inactivitySeconds,
                            hrvSdnnMs = window.hrvSdnnMs,
                            hrvRmssdMs = window.hrvRmssdMs,
                            hrvAvailable = window.isHrvAvailable,
                            windowCountProcessed = current.windowCountProcessed + 1
                        )
                    }
                }
            }
        }
    }

    fun startMonitoring(mode: AppMode) {
        sensorRepository?.startMonitoring(mode)
        _uiState.update { it.copy(isMonitoring = true, appMode = mode) }
    }

    fun stopMonitoring() {
        sensorRepository?.stopMonitoring()
        _uiState.update { it.copy(isMonitoring = false) }
    }
}
