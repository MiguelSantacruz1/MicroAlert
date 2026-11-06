package com.microalert.app.ml

import com.microalert.app.domain.model.FeatureVector

/**
 * Estrategia de imputación transparente para variables no disponibles (HRV, Reacción, Frecuencia Cardíaca).
 * Utiliza las medianas calculadas durante el entrenamiento del modelo.
 */
class ImputationStrategy(
    val trainingMedians: FloatArray = floatArrayOf(
        72.0f,  // 0: heartRate
        9.81f,  // 1: accelerometerMagnitude
        0.05f,  // 2: gyroscopeMagnitude
        10.0f,  // 3: inactivityDurationSeconds
        350.0f, // 4: reactionTimeMs
        600.0f, // 5: sessionDurationSeconds
        45.0f,  // 6: hrvSdnnMs
        40.0f   // 7: hrvRmssdMs
    )
) {
    fun imputeVector(rawVector: FeatureVector): FloatArray {
        return rawVector.toFloatArray(trainingMedians)
    }
}
