package com.microalert.app.sensors

import com.microalert.app.sensors.model.SensorSample

/**
 * Filtro y evaluador de calidad de señal fisiológica.
 * Identifica lecturas fisiológicamente imposibles o artefactos de movimiento.
 */
class SignalQualityEvaluator {

    fun isHeartRateValid(bpm: Float?): Boolean {
        if (bpm == null) return false
        // Rango fisiológico estándar (30 a 220 BPM)
        return bpm in 30.0f..220.0f
    }

    fun isAccelValid(magnitude: Float): Boolean {
        // La aceleración no debe ser negativa ni excesivamente alta (artefacto extremo > 50 m/s²)
        return magnitude in 0.0f..50.0f
    }

    fun isGyroValid(magnitude: Float): Boolean {
        // La velocidad angular no debe ser negativa ni sobrepasar 25 rad/s
        return magnitude in 0.0f..25.0f
    }

    fun evaluateSample(rawSample: SensorSample): SensorSample {
        val hrValid = isHeartRateValid(rawSample.heartRateBpm)
        val accelValid = isAccelValid(rawSample.accelMagnitude)
        val gyroValid = isGyroValid(rawSample.gyroMagnitude)

        return rawSample.copy(
            heartRateBpm = if (hrValid) rawSample.heartRateBpm else null,
            isHeartRateValid = hrValid,
            isAccelValid = accelValid,
            isGyroValid = gyroValid
        )
    }
}
