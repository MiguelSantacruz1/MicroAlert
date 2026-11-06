package com.microalert.app.domain.model

/**
 * Representa la calidad y disponibilidad de las señales capturadas en una ventana de evaluación.
 * Permite informar al usuario con transparencia qué datos son reales y cuáles fueron imputados.
 */
data class WindowDataQuality(
    val isHeartRateReal: Boolean,
    val isAccelerometerReal: Boolean,
    val isGyroscopeReal: Boolean,
    val isInactivityReal: Boolean,
    val isReactionTimeReal: Boolean,
    val isSessionDurationReal: Boolean,
    val isHrvSdnnReal: Boolean,
    val isHrvRmssdReal: Boolean
) {
    val realFeaturesCount: Int = listOf(
        isHeartRateReal,
        isAccelerometerReal,
        isGyroscopeReal,
        isInactivityReal,
        isReactionTimeReal,
        isSessionDurationReal,
        isHrvSdnnReal,
        isHrvRmssdReal
    ).count { it }

    val qualityPercentage: Float = (realFeaturesCount / 8.0f) * 100.0f

    val isHrvAvailable: Boolean
        get() = isHrvSdnnReal && isHrvRmssdReal

    companion object {
        fun fromFeatureVector(vector: FeatureVector): WindowDataQuality {
            return WindowDataQuality(
                isHeartRateReal = vector.heartRate != null,
                isAccelerometerReal = vector.accelerometerMagnitude > 0f,
                isGyroscopeReal = true,
                isInactivityReal = true,
                isReactionTimeReal = vector.reactionTimeMs != null,
                isSessionDurationReal = true,
                isHrvSdnnReal = vector.hrvSdnnMs != null,
                isHrvRmssdReal = vector.hrvRmssdMs != null
            )
        }
    }
}
