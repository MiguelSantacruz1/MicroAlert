package com.microalert.app.domain.usecase

import com.microalert.app.data.repository.SensorRepository
import com.microalert.app.sensors.model.SensorSample
import kotlinx.coroutines.flow.Flow

class ObserveSensorDataUseCase(
    private val sensorRepository: SensorRepository
) {
    operator fun invoke(): Flow<SensorSample> {
        return sensorRepository.getSensorDataStream()
    }
}
