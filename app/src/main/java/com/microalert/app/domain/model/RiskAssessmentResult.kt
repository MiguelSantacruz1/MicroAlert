package com.microalert.app.domain.model

/**
 * Resultado completo de la evaluación de riesgo de una ventana temporal.
 */
data class RiskAssessmentResult(
    val timestampMs: Long = System.currentTimeMillis(),
    val drowsinessProbability: Float,
    val rawRiskLevel: RiskLevel,
    val effectiveRiskLevel: RiskLevel,
    val consecutiveElevatedWindows: Int,
    val dataQuality: WindowDataQuality
) {
    val isAlertTriggered: Boolean
        get() = effectiveRiskLevel == RiskLevel.HIGH || effectiveRiskLevel == RiskLevel.CRITICAL
}
