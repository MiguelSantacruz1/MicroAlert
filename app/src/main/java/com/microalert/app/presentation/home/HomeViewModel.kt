package com.microalert.app.presentation.home

import androidx.lifecycle.ViewModel
import com.microalert.app.data.repository.SensorRepository
import com.microalert.app.domain.model.AppMode
import com.microalert.app.domain.usecase.CheckSensorAvailabilityUseCase
import com.microalert.app.presentation.settings.SettingsStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HomeViewModel(
    private val checkSensorAvailabilityUseCase: CheckSensorAvailabilityUseCase? = null,
    private val sensorRepository: SensorRepository? = null,
    private val store: SettingsStore? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(appMode = store?.appMode() ?: AppMode.DEMO))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        refreshSensors()
    }

    fun toggleAppMode() {
        _uiState.update { currentState ->
            val nextMode = if (currentState.appMode == AppMode.REAL) AppMode.DEMO else AppMode.REAL
            currentState.copy(appMode = nextMode)
        }
        store?.saveAppMode(_uiState.value.appMode)
        refreshSensors()
    }

    fun refreshSensors() {
        val useCase = checkSensorAvailabilityUseCase ?: return
        sensorRepository?.selectMode(_uiState.value.appMode)
        val result = useCase()
        val available = listOf(
            result.isHeartRateAvailable,
            result.isAccelerometerAvailable,
            result.isGyroscopeAvailable,
            result.isHrvAvailable
        ).count { it }
        _uiState.update { it.copy(activeSensorsCount = available) }
    }
}
