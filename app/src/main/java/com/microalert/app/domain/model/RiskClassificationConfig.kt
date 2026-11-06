package com.microalert.app.domain.model

/**
 * Parámetros configurables de la política de clasificación de riesgo de somnolencia.
 * Permite ajustar umbrales de probabilidad, persistencia temporal e histéresis.
 */
data class RiskClassificationConfig(
    val precautionThreshold: Float = 0.30f,
    val highThreshold: Float = 0.60f,
    val criticalThreshold: Float = 0.85f,
    val persistenceWindowsRequired: Int = 2,
    val hysteresisDelta: Float = 0.05f
) {
    init {
        require(precautionThreshold < highThreshold) {
            "precautionThreshold debe ser menor que highThreshold"
        }
        require(highThreshold < criticalThreshold) {
            "highThreshold debe ser menor que criticalThreshold"
        }
        require(persistenceWindowsRequired >= 1) {
            "persistenceWindowsRequired debe ser al menos 1"
        }
        require(hysteresisDelta >= 0.0f && hysteresisDelta < precautionThreshold) {
            "hysteresisDelta debe ser no negativo y menor que precautionThreshold"
        }
    }
}
