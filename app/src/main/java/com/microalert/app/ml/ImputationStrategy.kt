package com.microalert.app.ml

import com.microalert.app.domain.model.FeatureVector

/**
 * Estrategia de imputación transparente para variables no disponibles (HRV, Reacción, Frecuencia Cardíaca).
 * Utiliza las medianas calculadas durante el entrenamiento del modelo.
 */
class ImputationStrategy(
    val trainingMedians: FloatArray = floatArrayOf(
        70.45f,  // 0: heartRate (BPM)
        9.81f,   // 1: accelerometerMagnitude (m/s²)
        0.04f,   // 2: gyroscopeMagnitude (rad/s)
        17.80f,  // 3: inactivityDurationSeconds (s)
        357.55f, // 4: reactionTimeMs (ms)
        915.0f,  // 5: sessionDurationSeconds (s)
        49.46f,  // 6: hrvSdnnMs (ms)
        44.60f   // 7: hrvRmssdMs (ms)
    )
) {
    fun imputeVector(rawVector: FeatureVector): FloatArray {
        return rawVector.toFloatArray(trainingMedians)
    }
}
