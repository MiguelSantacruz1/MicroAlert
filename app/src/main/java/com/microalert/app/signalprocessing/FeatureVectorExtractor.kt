package com.microalert.app.signalprocessing

import com.microalert.app.domain.model.FeatureVector
import com.microalert.app.signalprocessing.model.ProcessedWindowData

/**
 * Ensamblador del Vector de 8 Características a partir de la ventana procesada y estados del sistema.
 */
class FeatureVectorExtractor {

    fun extractFeatureVector(
        processedWindow: ProcessedWindowData,
        reactionTimeMs: Float?,
        sessionDurationSeconds: Float
    ): FeatureVector {
        return FeatureVector(
            heartRate = processedWindow.avgHeartRateBpm,
            accelerometerMagnitude = processedWindow.avgAccelerometerMagnitude,
            gyroscopeMagnitude = processedWindow.avgGyroscopeMagnitude,
            inactivityDurationSeconds = processedWindow.inactivitySeconds.toFloat(),
            reactionTimeMs = reactionTimeMs,
            sessionDurationSeconds = sessionDurationSeconds,
            hrvSdnnMs = processedWindow.hrvSdnnMs,
            hrvRmssdMs = processedWindow.hrvRmssdMs
        )
    }
}
