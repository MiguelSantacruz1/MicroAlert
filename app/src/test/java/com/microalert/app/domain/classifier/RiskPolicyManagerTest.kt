package com.microalert.app.domain.classifier

import com.microalert.app.domain.model.RiskClassificationConfig
import com.microalert.app.domain.model.RiskLevel
import com.microalert.app.domain.model.WindowDataQuality
import org.junit.Assert.assertEquals
import org.junit.Test

class RiskPolicyManagerTest {

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
    fun `evaluateProbability should map exact thresholds to corresponding RiskLevel`() {
        // Con persistence = 1 probamos mapeo directo de umbrales
        val manager = RiskPolicyManager(
            config = RiskClassificationConfig(
                precautionThreshold = 0.30f,
                highThreshold = 0.60f,
                criticalThreshold = 0.85f,
                persistenceWindowsRequired = 1,
                hysteresisDelta = 0.0f
            )
        )

        assertEquals(RiskLevel.NORMAL, manager.evaluateProbability(0.10f, dummyQuality).effectiveRiskLevel)
        assertEquals(RiskLevel.NORMAL, manager.evaluateProbability(0.29f, dummyQuality).effectiveRiskLevel)

        assertEquals(RiskLevel.PRECAUTION, manager.evaluateProbability(0.30f, dummyQuality).effectiveRiskLevel)
        assertEquals(RiskLevel.PRECAUTION, manager.evaluateProbability(0.59f, dummyQuality).effectiveRiskLevel)

        assertEquals(RiskLevel.HIGH, manager.evaluateProbability(0.60f, dummyQuality).effectiveRiskLevel)
        assertEquals(RiskLevel.HIGH, manager.evaluateProbability(0.84f, dummyQuality).effectiveRiskLevel)

        assertEquals(RiskLevel.CRITICAL, manager.evaluateProbability(0.85f, dummyQuality).effectiveRiskLevel)
        assertEquals(RiskLevel.CRITICAL, manager.evaluateProbability(0.99f, dummyQuality).effectiveRiskLevel)
    }
}
