package com.microalert.app.domain.classifier

import com.microalert.app.domain.model.RiskClassificationConfig
import com.microalert.app.domain.model.RiskLevel
import com.microalert.app.domain.model.WindowDataQuality
import org.junit.Assert.assertEquals
import org.junit.Test

class RiskPersistenceTest {

    private val dummyQuality = WindowDataQuality(
        isHeartRateReal = true,
        isAccelerometerReal = true,
        isGyroscopeReal = true,
        isInactivityReal = true,
        isReactionTimeReal = true,
        isSessionDurationReal = true,
        isHrvSdnnReal = true,
        isHrvRmssdReal = true
    )

    @Test
    fun `isolated single high risk window should be filtered to precaution until persistence threshold is reached`() {
        val manager = RiskPolicyManager(
            config = RiskClassificationConfig(
                precautionThreshold = 0.30f,
                highThreshold = 0.60f,
                criticalThreshold = 0.85f,
                persistenceWindowsRequired = 2,
                hysteresisDelta = 0.0f
            )
        )

        // Ventana 1: Salto brusco a 0.90 (CRITICAL) -> Filtro retiene en PRECAUTION
        val res1 = manager.evaluateProbability(0.90f, dummyQuality)
        assertEquals(RiskLevel.CRITICAL, res1.rawRiskLevel)
        assertEquals(RiskLevel.PRECAUTION, res1.effectiveRiskLevel)
        assertEquals(1, res1.consecutiveElevatedWindows)

        // Ventana 2: Persistencia confirmada a 0.90 (CRITICAL) -> Escala a CRITICAL
        val res2 = manager.evaluateProbability(0.90f, dummyQuality)
        assertEquals(RiskLevel.CRITICAL, res2.rawRiskLevel)
        assertEquals(RiskLevel.CRITICAL, res2.effectiveRiskLevel)
        assertEquals(2, res2.consecutiveElevatedWindows)

        // Ventana 3: Retorno a NORMAL -> Resetea contador consecutivo
        val res3 = manager.evaluateProbability(0.15f, dummyQuality)
        assertEquals(RiskLevel.NORMAL, res3.rawRiskLevel)
        assertEquals(RiskLevel.NORMAL, res3.effectiveRiskLevel)
        assertEquals(0, res3.consecutiveElevatedWindows)

        // Ventana 4: Salto a 0.70 (HIGH) -> Primer evento elevado retiene en PRECAUTION
        val res4 = manager.evaluateProbability(0.70f, dummyQuality)
        assertEquals(RiskLevel.HIGH, res4.rawRiskLevel)
        assertEquals(RiskLevel.PRECAUTION, res4.effectiveRiskLevel)
        assertEquals(1, res4.consecutiveElevatedWindows)

        // Ventana 5: Segundo evento consecutivo a 0.70 (HIGH) -> Escala a HIGH
        val res5 = manager.evaluateProbability(0.70f, dummyQuality)
        assertEquals(RiskLevel.HIGH, res5.rawRiskLevel)
        assertEquals(RiskLevel.HIGH, res5.effectiveRiskLevel)
        assertEquals(2, res5.consecutiveElevatedWindows)
    }
}
