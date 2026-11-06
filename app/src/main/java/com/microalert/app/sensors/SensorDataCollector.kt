package com.microalert.app.sensors

import android.content.Context
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

/**
 * Colector compuesto que consolida lecturas de los gestores reales (HeartRate, Accelerometer, Gyroscope).
 */
class SensorDataCollector(context: Context) : SensorDataSource {

    private val heartRateManager = HeartRateSensorManager(context)
    private val accelManager = AccelerometerSensorManager(context)
    private val gyroManager = GyroscopeSensorManager(context)
    private val hrvExtractor = HrvIntervalExtractor(context)
    private val evaluator = SignalQualityEvaluator()

    private val _sensorDataStream = MutableStateFlow(SensorSample())
    override val sensorDataStream: Flow<SensorSample> = _sensorDataStream.asStateFlow()

    private var collectionJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun startSampling() {
        heartRateManager.startListening()
        accelManager.startListening()
        gyroManager.startListening()

        if (collectionJob == null || collectionJob?.isCancelled == true) {
            collectionJob = scope.launch {
                while (isActive) {
                    val bpm = heartRateManager.bpmFlow.value
                    val accel = accelManager.accelFlow.value
                    val gyro = gyroManager.gyroFlow.value

                    val rawSample = SensorSample(
                        timestampMs = System.currentTimeMillis(),
                        heartRateBpm = bpm,
                        rIntervalsMs = hrvExtractor.extractRIntervalsMs(),
                        accelX = accel.x,
                        accelY = accel.y,
                        accelZ = accel.z,
                        accelMagnitude = accel.magnitude,
                        gyroX = gyro.x,
                        gyroY = gyro.y,
                        gyroZ = gyro.z,
                        gyroMagnitude = gyro.magnitude
                    )

                    _sensorDataStream.value = evaluator.evaluateSample(rawSample)
                    delay(500L) // Muestreo regular cada 500ms
                }
            }
        }
    }

    override fun stopSampling() {
        collectionJob?.cancel()
        collectionJob = null
        heartRateManager.stopListening()
        accelManager.stopListening()
        gyroManager.stopListening()
    }

    override fun isHeartRateAvailable(): Boolean = heartRateManager.isSensorAvailable()
    override fun isAccelerometerAvailable(): Boolean = accelManager.isSensorAvailable()
    override fun isGyroscopeAvailable(): Boolean = gyroManager.isSensorAvailable()
    override fun isHrvAvailable(): Boolean = hrvExtractor.isHrvSupportedOnDevice()
}
