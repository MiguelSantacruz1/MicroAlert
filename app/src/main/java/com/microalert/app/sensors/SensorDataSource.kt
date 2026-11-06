package com.microalert.app.sensors

import com.microalert.app.sensors.model.SensorSample
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz unificada para fuentes de sensores (Hardware Real y Simulador DEMO).
 */
interface SensorDataSource {
    val sensorDataStream: Flow<SensorSample>
    fun startSampling()
    fun stopSampling()
    fun isHeartRateAvailable(): Boolean
    fun isAccelerometerAvailable(): Boolean
    fun isGyroscopeAvailable(): Boolean
    fun isHrvAvailable(): Boolean
}
