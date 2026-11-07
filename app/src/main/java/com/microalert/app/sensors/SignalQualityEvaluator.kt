package com.microalert.app.sensors

import com.microalert.app.sensors.model.SensorSample

class SignalQualityEvaluator {

    fun isHeartRateValid(bpm: Float?): Boolean {
        if (bpm == null) return false

        return bpm in 30.0f..220.0f
    }

    fun isAccelValid(magnitude: Float): Boolean {

        return magnitude in 0.0f..50.0f
    }

    fun isGyroValid(magnitude: Float): Boolean {

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
