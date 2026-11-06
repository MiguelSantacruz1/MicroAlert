package com.microalert.app.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Gestor de captura de frecuencia cardíaca (BPM) utilizando Android SensorManager (Sensor.TYPE_HEART_RATE).
 */
class HeartRateSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE)

    private val _bpmFlow = MutableStateFlow<Float?>(null)
    val bpmFlow: StateFlow<Float?> = _bpmFlow.asStateFlow()

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    fun isSensorAvailable(): Boolean = heartRateSensor != null

    fun startListening() {
        if (heartRateSensor != null && !_isListening.value) {
            sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL)
            _isListening.value = true
        }
    }

    fun stopListening() {
        if (_isListening.value) {
            sensorManager.unregisterListener(this)
            _isListening.value = false
            _bpmFlow.value = null
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_HEART_RATE && event.values.isNotEmpty()) {
            val bpm = event.values[0]
            if (bpm > 0f) {
                _bpmFlow.value = bpm
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Manejo opcional de cambios de precisión de señal
    }
}
