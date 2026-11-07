package com.microalert.app.domain.model

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
