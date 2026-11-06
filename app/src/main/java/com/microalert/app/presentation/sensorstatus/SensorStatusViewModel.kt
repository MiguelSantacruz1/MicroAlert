package com.microalert.app.presentation.sensorstatus

import androidx.lifecycle.ViewModel
import com.microalert.app.domain.usecase.CheckSensorAvailabilityUseCase
import com.microalert.app.sensors.PermissionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel para gestionar el diagnóstico dinámico de sensores y verificación de permisos.
 */
class SensorStatusViewModel(
    private val checkSensorAvailabilityUseCase: CheckSensorAvailabilityUseCase? = null,
    private val permissionManager: PermissionManager? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(SensorStatusUiState())
    val uiState: StateFlow<SensorStatusUiState> = _uiState.asStateFlow()

    fun refreshStatus() {
        checkSensorAvailabilityUseCase?.let { useCase ->
            val availability = useCase()
            val hasBodySensors = permissionManager?.hasBodySensorsPermission() ?: false
            val hasActivityRec = permissionManager?.hasActivityRecognitionPermission() ?: false

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
