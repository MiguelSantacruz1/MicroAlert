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
                persistenceWindowsRequired = 1,
                hysteresisDelta = 0.05f
            )
        )

        val res1 = manager.evaluateProbability(0.70f, dummyQuality)
        assertEquals(RiskLevel.HIGH, res1.effectiveRiskLevel)

        val res2 = manager.evaluateProbability(0.58f, dummyQuality)
        assertEquals(RiskLevel.PRECAUTION, res2.rawRiskLevel)
        assertEquals(RiskLevel.HIGH, res2.effectiveRiskLevel)

        val res3 = manager.evaluateProbability(0.53f, dummyQuality)
        assertEquals(RiskLevel.PRECAUTION, res3.effectiveRiskLevel)

        val res4 = manager.evaluateProbability(0.28f, dummyQuality)
        assertEquals(RiskLevel.NORMAL, res4.rawRiskLevel)
        assertEquals(RiskLevel.PRECAUTION, res4.effectiveRiskLevel)

        val res5 = manager.evaluateProbability(0.20f, dummyQuality)
        assertEquals(RiskLevel.NORMAL, res5.effectiveRiskLevel)
    }
}
