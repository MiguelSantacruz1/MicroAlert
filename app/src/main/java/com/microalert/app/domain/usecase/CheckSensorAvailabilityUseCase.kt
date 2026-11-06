package com.microalert.app.domain.usecase

import com.microalert.app.data.repository.SensorRepository

data class SensorAvailabilityResult(
    val isHeartRateAvailable: Boolean,
    val isAccelerometerAvailable: Boolean,
    val isGyroscopeAvailable: Boolean,
    val isHrvAvailable: Boolean
)

/**
 * Caso de uso para obtener el diagnóstico completo de disponibilidad de sensores de hardware.
 */
class CheckSensorAvailabilityUseCase(
    private val sensorRepository: SensorRepository
) {
    operator fun invoke(): SensorAvailabilityResult {
        return SensorAvailabilityResult(
            isHeartRateAvailable = sensorRepository.isHeartRateAvailable(),
            isAccelerometerAvailable = sensorRepository.isAccelerometerAvailable(),
            isGyroscopeAvailable = sensorRepository.isGyroscopeAvailable(),
            isHrvAvailable = sensorRepository.isHrvAvailable()
        )
    }
}
