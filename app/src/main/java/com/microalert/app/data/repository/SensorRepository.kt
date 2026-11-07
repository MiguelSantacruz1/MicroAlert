package com.microalert.app.data.repository

import com.microalert.app.domain.model.AppMode
import com.microalert.app.sensors.model.SensorSample
import kotlinx.coroutines.flow.Flow

interface SensorRepository {
    fun getSensorDataStream(): Flow<SensorSample>
    fun startMonitoring(appMode: AppMode)
    fun stopMonitoring()
    fun isHeartRateAvailable(): Boolean
    fun isAccelerometerAvailable(): Boolean
    fun isGyroscopeAvailable(): Boolean
    fun isHrvAvailable(): Boolean
}
