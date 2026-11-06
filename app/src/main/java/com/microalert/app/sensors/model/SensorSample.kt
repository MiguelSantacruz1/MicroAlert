package com.microalert.app.sensors.model

/**
 * Representa una muestra individual capturada por los sensores del smartwatch.
 */
data class SensorSample(
    val timestampMs: Long = System.currentTimeMillis(),
    val heartRateBpm: Float? = null,
    val rIntervalsMs: List<Float> = emptyList(),
    val accelX: Float = 0f,
    val accelY: Float = 0f,
    val accelZ: Float = 0f,
    val accelMagnitude: Float = 0f,
    val gyroX: Float = 0f,
    val gyroY: Float = 0f,
    val gyroZ: Float = 0f,
    val gyroMagnitude: Float = 0f,
    val isHeartRateValid: Boolean = false,
    val isAccelValid: Boolean = false,
    val isGyroValid: Boolean = false
)
