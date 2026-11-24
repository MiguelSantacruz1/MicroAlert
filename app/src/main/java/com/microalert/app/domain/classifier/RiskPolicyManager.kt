package com.microalert.app.domain.classifier

import com.microalert.app.domain.model.RiskAssessmentResult
import com.microalert.app.domain.model.RiskClassificationConfig
import com.microalert.app.domain.model.RiskLevel
import com.microalert.app.domain.model.WindowDataQuality

class RiskPolicyManager(
    val config: RiskClassificationConfig = RiskClassificationConfig()
) {
    /** Desplazamiento de umbrales por calibración personal (0 = umbrales de fábrica). */
    var thresholdOffset: Float = 0f

    private val precautionThr: Float get() = config.precautionThreshold + thresholdOffset
    private val highThr: Float get() = config.highThreshold + thresholdOffset
    private val criticalThr: Float get() = config.criticalThreshold + thresholdOffset

    private var currentEffectiveLevel: RiskLevel = RiskLevel.NORMAL
    private var consecutiveElevatedCount: Int = 0

    fun evaluateProbability(
        probability: Float,
        dataQuality: WindowDataQuality,
        timestampMs: Long = System.currentTimeMillis()
    ): RiskAssessmentResult {
        val rawLevel = determineRawLevel(probability)

        if (rawLevel == RiskLevel.HIGH || rawLevel == RiskLevel.CRITICAL) {
            consecutiveElevatedCount++
        } else {
            consecutiveElevatedCount = 0
        }

        val proposedLevel = when {
            rawLevel == RiskLevel.CRITICAL -> {
                if (consecutiveElevatedCount >= config.persistenceWindowsRequired) {
                    RiskLevel.CRITICAL
                } else {
                    RiskLevel.PRECAUTION
                }
            }
            rawLevel == RiskLevel.HIGH -> {
                if (consecutiveElevatedCount >= config.persistenceWindowsRequired) {
                    RiskLevel.HIGH
                } else {
                    RiskLevel.PRECAUTION
                }
            }
            rawLevel == RiskLevel.PRECAUTION -> RiskLevel.PRECAUTION
            else -> RiskLevel.NORMAL
        }

        val finalEffectiveLevel = applyHysteresis(probability, proposedLevel)
        currentEffectiveLevel = finalEffectiveLevel

        return RiskAssessmentResult(
            timestampMs = timestampMs,
            drowsinessProbability = probability,
            rawRiskLevel = rawLevel,
            effectiveRiskLevel = finalEffectiveLevel,
            consecutiveElevatedWindows = consecutiveElevatedCount,
            dataQuality = dataQuality
        )
    }

    private fun determineRawLevel(p: Float): RiskLevel {
        return when {
            p >= criticalThr -> RiskLevel.CRITICAL
            p >= highThr -> RiskLevel.HIGH
            p >= precautionThr -> RiskLevel.PRECAUTION
            else -> RiskLevel.NORMAL
        }
    }

    private fun applyHysteresis(probability: Float, proposedLevel: RiskLevel): RiskLevel {

        if (proposedLevel.ordinal >= currentEffectiveLevel.ordinal) {
            return proposedLevel
        }

        return when (currentEffectiveLevel) {
            RiskLevel.CRITICAL -> {
                if (probability < (criticalThr - config.hysteresisDelta)) {
                    proposedLevel
                } else {
                    RiskLevel.CRITICAL
                }
            }
            RiskLevel.HIGH -> {
                if (probability < (highThr - config.hysteresisDelta)) {
                    proposedLevel
                } else {
                    RiskLevel.HIGH
                }
            }
            RiskLevel.PRECAUTION -> {
                if (probability < (precautionThr - config.hysteresisDelta)) {
                    proposedLevel
                } else {
                    RiskLevel.PRECAUTION
                }
            }
            RiskLevel.NORMAL -> RiskLevel.NORMAL
        }
    }

    fun reset() {
        thresholdOffset = 0f
        currentEffectiveLevel = RiskLevel.NORMAL
        consecutiveElevatedCount = 0
    }

    fun getCurrentEffectiveLevel(): RiskLevel = currentEffectiveLevel
    fun getConsecutiveElevatedCount(): Int = consecutiveElevatedCount
}
