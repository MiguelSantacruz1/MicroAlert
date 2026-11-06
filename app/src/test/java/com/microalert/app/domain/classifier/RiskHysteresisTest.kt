package com.microalert.app.domain.classifier

import com.microalert.app.domain.model.RiskClassificationConfig
import com.microalert.app.domain.model.RiskLevel
import com.microalert.app.domain.model.WindowDataQuality
import org.junit.Assert.assertEquals
import org.junit.Test

class RiskHysteresisTest {

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
    fun `hysteresis should prevent immediate descent when probability drops within deadband`() {
        val manager = RiskPolicyManager(
            config = RiskClassificationConfig(
                precautionThreshold = 0.30f,
                highThreshold = 0.60f,
                criticalThreshold = 0.85f,
                persistenceWindowsRequired = 1, // persistencia inmediata para aislar prueba de histéresis
                hysteresisDelta = 0.05f
            )
        )

        // 1. Elevar estado a HIGH (P = 0.70)
        val res1 = manager.evaluateProbability(0.70f, dummyQuality)
        assertEquals(RiskLevel.HIGH, res1.effectiveRiskLevel)

        // 2. Probabilidad baja a 0.58f. Raw = PRECAUTION (< 0.60), pero 0.58 >= (0.60 - 0.05 = 0.55)
        // Por histéresis, debe mantenerse en HIGH
        val res2 = manager.evaluateProbability(0.58f, dummyQuality)
        assertEquals(RiskLevel.PRECAUTION, res2.rawRiskLevel)
        assertEquals(RiskLevel.HIGH, res2.effectiveRiskLevel)

        // 3. Probabilidad baja a 0.53f (< 0.55). Cruza la zona muerta de histéresis, desciende a PRECAUTION
        val res3 = manager.evaluateProbability(0.53f, dummyQuality)
        assertEquals(RiskLevel.PRECAUTION, res3.effectiveRiskLevel)

        // 4. Probabilidad baja a 0.28f (< 0.30 pero >= 0.25). Por histéresis se mantiene en PRECAUTION
        val res4 = manager.evaluateProbability(0.28f, dummyQuality)
        assertEquals(RiskLevel.NORMAL, res4.rawRiskLevel)
        assertEquals(RiskLevel.PRECAUTION, res4.effectiveRiskLevel)

        // 5. Probabilidad baja a 0.20f (< 0.25). Desciende a NORMAL
        val res5 = manager.evaluateProbability(0.20f, dummyQuality)
        assertEquals(RiskLevel.NORMAL, res5.effectiveRiskLevel)
    }
}
