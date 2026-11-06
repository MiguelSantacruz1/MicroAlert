package com.microalert.app.domain.model

/**
 * Representa el Vector de 8 Características utilizado para la clasificación con Random Forest.
 * Mantiene de forma estricta el orden exigido por la especificación:
 * 1. heartRate (BPM)
 * 2. accelerometerMagnitude (m/s²)
 * 3. gyroscopeMagnitude (rad/s)
 * 4. inactivityDurationSeconds (s)
 * 5. reactionTimeMs (ms)
 * 6. sessionDurationSeconds (s)
 * 7. hrvSdnnMs (ms)
 * 8. hrvRmssdMs (ms)
 */
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
    /**
     * Convierte el vector a un arreglo de flotantes con orden estricto.
     * Utilizado para alimentar el evaluador de Random Forest.
     */
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
