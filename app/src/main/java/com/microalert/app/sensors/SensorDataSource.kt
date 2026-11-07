package com.microalert.app.sensors

import com.microalert.app.sensors.model.SensorSample
import kotlinx.coroutines.flow.Flow

interface SensorDataSource {
    val sensorDataStream: Flow<SensorSample>
    fun startSampling()
    fun stopSampling()
    fun isHeartRateAvailable(): Boolean
    fun isAccelerometerAvailable(): Boolean
    fun isGyroscopeAvailable(): Boolean
    fun isHrvAvailable(): Boolean
}
