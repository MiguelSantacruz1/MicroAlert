package com.microalert.app.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sqrt

data class GyroData(
    val x: Float = 0f,
    val y: Float = 0f,
    val z: Float = 0f,
    val magnitude: Float = 0f
)

class GyroscopeSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gyroSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

    private val _gyroFlow = MutableStateFlow(GyroData())
    val gyroFlow: StateFlow<GyroData> = _gyroFlow.asStateFlow()

    private var isListening = false

    fun isSensorAvailable(): Boolean = gyroSensor != null

    fun startListening() {
        if (gyroSensor != null && !isListening) {
            sensorManager.registerListener(this, gyroSensor, SensorManager.SENSOR_DELAY_GAME)
            isListening = true
        }
    }

    fun stopListening() {
        if (isListening) {
            sensorManager.unregisterListener(this)
            isListening = false
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE && event.values.size >= 3) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            val mag = sqrt(x * x + y * y + z * z)
            _gyroFlow.value = GyroData(x = x, y = y, z = z, magnitude = mag)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
