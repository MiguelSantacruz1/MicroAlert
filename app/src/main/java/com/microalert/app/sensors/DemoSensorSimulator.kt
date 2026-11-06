package com.microalert.app.sensors

import com.microalert.app.sensors.model.SensorSample
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Simulador de datos de sensores para el MODO DEMO.
 * Genera métricas fisiológicas sintéticas realistas para pruebas sin sensores de hardware.
 */
class DemoSensorSimulator : SensorDataSource {

    private val _sensorDataStream = MutableStateFlow(SensorSample())
    override val sensorDataStream: Flow<SensorSample> = _sensorDataStream.asStateFlow()

    private var simulationJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun startSampling() {
        if (simulationJob == null || simulationJob?.isCancelled == true) {
            simulationJob = scope.launch {
                var baseBpm = 70.0f
                while (isActive) {
                    baseBpm += Random.nextFloat() * 2f - 1f
                    baseBpm = baseBpm.coerceIn(55f, 95f)

                    val accelMag = 9.81f + (Random.nextFloat() * 0.4f - 0.2f)
                    val gyroMag = (Random.nextFloat() * 0.08f).coerceAtLeast(0.01f)

                    _sensorDataStream.value = SensorSample(
                        timestampMs = System.currentTimeMillis(),
                        heartRateBpm = baseBpm,
                        rIntervalsMs = listOf(820f, 835f, 810f, 825f),
                        accelX = 0.1f,
                        accelY = 9.8f,
                        accelZ = 0.2f,
                        accelMagnitude = accelMag,
                        gyroX = 0.01f,
                        gyroY = 0.02f,
                        gyroZ = 0.01f,
                        gyroMagnitude = gyroMag,
                        isHeartRateValid = true,
                        isAccelValid = true,
                        isGyroValid = true
                    )
                    delay(1000L)
                }
            }
        }
    }

    override fun stopSampling() {
        simulationJob?.cancel()
        simulationJob = null
    }

    override fun isHeartRateAvailable(): Boolean = true
    override fun isAccelerometerAvailable(): Boolean = true
    override fun isGyroscopeAvailable(): Boolean = true
    override fun isHrvAvailable(): Boolean = true
}
