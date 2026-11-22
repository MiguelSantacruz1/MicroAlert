package com.microalert.app.presentation.sensorstatus

import androidx.lifecycle.ViewModel
import com.microalert.app.domain.usecase.CheckSensorAvailabilityUseCase
import com.microalert.app.sensors.PermissionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SensorStatusViewModel(
    private val checkSensorAvailabilityUseCase: CheckSensorAvailabilityUseCase? = null,
    private val permissionManager: PermissionManager? = null,
    private val sensorRepository: com.microalert.app.data.repository.SensorRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(SensorStatusUiState())
    val uiState: StateFlow<SensorStatusUiState> = _uiState.asStateFlow()

    init {
        refreshStatus()
    }

    fun refreshStatus() {
        checkSensorAvailabilityUseCase?.let { useCase ->
            val availability = useCase()
            val isDemo = sensorRepository?.currentMode() == com.microalert.app.domain.model.AppMode.DEMO
            val hasBodySensors = isDemo || (permissionManager?.hasBodySensorsPermission() ?: false)
            val hasActivityRec = isDemo || (permissionManager?.hasActivityRecognitionPermission() ?: false)

            _uiState.update { current ->
                current.copy(
                    isHeartRateAvailable = availability.isHeartRateAvailable,
                    isAccelerometerAvailable = availability.isAccelerometerAvailable,
                    isGyroscopeAvailable = availability.isGyroscopeAvailable,
                    isHrvAvailable = availability.isHrvAvailable,
                    hasBodySensorsPermission = hasBodySensors,
                    hasActivityRecognitionPermission = hasActivityRec
                )
            }
        }
    }
}
