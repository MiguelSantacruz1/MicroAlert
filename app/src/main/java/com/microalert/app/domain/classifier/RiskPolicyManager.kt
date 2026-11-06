package com.microalert.app.domain.classifier

import com.microalert.app.domain.model.RiskAssessmentResult
import com.microalert.app.domain.model.RiskClassificationConfig
import com.microalert.app.domain.model.RiskLevel
import com.microalert.app.domain.model.WindowDataQuality

/**
 * Gestor de la política de clasificación de riesgo de somnolencia.
 * Aplica umbrales configurables, filtro de persistencia temporal (consecutive windows)
 * e histéresis para evitar oscilaciones rápidas entre niveles de alerta.
 */
class RiskPolicyManager(
    val config: RiskClassificationConfig = RiskClassificationConfig()
) {
    private var currentEffectiveLevel: RiskLevel = RiskLevel.NORMAL
    private var consecutiveElevatedCount: Int = 0

    fun evaluateProbability(
        probability: Float,
        dataQuality: WindowDataQuality,
        timestampMs: Long = System.currentTimeMillis()
    ): RiskAssessmentResult {
        val rawLevel = determineRawLevel(probability)

        // 1. Manejo de persistencia temporal para niveles elevados (HIGH y CRITICAL)
        if (rawLevel == RiskLevel.HIGH || rawLevel == RiskLevel.CRITICAL) {
            consecutiveElevatedCount++
        } else {
            consecutiveElevatedCount = 0
        }

        // 2. Determinar nivel propuesto según persistencia
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

        // 3. Aplicación de Histéresis al descender de nivel
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
            p >= config.criticalThreshold -> RiskLevel.CRITICAL
            p >= config.highThreshold -> RiskLevel.HIGH
            p >= config.precautionThreshold -> RiskLevel.PRECAUTION
            else -> RiskLevel.NORMAL
        }
    }

    private fun applyHysteresis(probability: Float, proposedLevel: RiskLevel): RiskLevel {
        // Solo aplica histéresis si el nivel propuesto es inferior al nivel actual
        if (proposedLevel.ordinal >= currentEffectiveLevel.ordinal) {
            return proposedLevel
        }

        // Condición de histéresis para descender de cada nivel
        return when (currentEffectiveLevel) {
            RiskLevel.CRITICAL -> {
                if (probability < (config.criticalThreshold - config.hysteresisDelta)) {
                    proposedLevel
                } else {
                    RiskLevel.CRITICAL
                }
            }
            RiskLevel.HIGH -> {
                if (probability < (config.highThreshold - config.hysteresisDelta)) {
                    proposedLevel
                } else {
                    RiskLevel.HIGH
                }
            }
            RiskLevel.PRECAUTION -> {
                if (probability < (config.precautionThreshold - config.hysteresisDelta)) {
                    proposedLevel
                } else {
                    RiskLevel.PRECAUTION
                }
            }
            RiskLevel.NORMAL -> RiskLevel.NORMAL
        }
    }

    fun reset() {
        currentEffectiveLevel = RiskLevel.NORMAL
        consecutiveElevatedCount = 0
    }

    fun getCurrentEffectiveLevel(): RiskLevel = currentEffectiveLevel
    fun getConsecutiveElevatedCount(): Int = consecutiveElevatedCount
}
