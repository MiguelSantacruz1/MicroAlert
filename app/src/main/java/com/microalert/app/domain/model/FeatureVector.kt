package com.microalert.app.domain.model

data class FeatureVector(
    val heartRate: Float?,
    val accelerometerMagnitude: Float,
    val gyroscopeMagnitude: Float,
    val inactivityDurationSeconds: Float,
    val reactionTimeMs: Float?,
    val sessionDurationSeconds: Float,
    val hrvSdnnMs: Float?,
    val hrvRmssdMs: Float?
) {

    fun toFloatArray(defaultMedians: FloatArray): FloatArray {
        require(defaultMedians.size == 8) { "El arreglo de medianas debe contener exactamente 8 elementos." }
        return floatArrayOf(
            heartRate ?: defaultMedians[0],
            accelerometerMagnitude,
            gyroscopeMagnitude,
            inactivityDurationSeconds,
            reactionTimeMs ?: defaultMedians[4],
            sessionDurationSeconds,
            hrvSdnnMs ?: defaultMedians[6],
            hrvRmssdMs ?: defaultMedians[7]
        )
    }
}
