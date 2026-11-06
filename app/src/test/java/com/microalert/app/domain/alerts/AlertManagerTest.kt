package com.microalert.app.domain.alerts

import com.microalert.app.domain.model.AlertPolicyConfig
import com.microalert.app.domain.model.RiskAssessmentResult
import com.microalert.app.domain.model.RiskLevel
import com.microalert.app.domain.model.WindowDataQuality
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AlertManagerTest {

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
    fun `processRiskAssessment with NORMAL risk level should not trigger alert`() {
        val manager = AlertManager()
        val assessment = RiskAssessmentResult(
            drowsinessProbability = 0.10f,
            rawRiskLevel = RiskLevel.NORMAL,
            effectiveRiskLevel = RiskLevel.NORMAL,
            consecutiveElevatedWindows = 0,
            dataQuality = dummyQuality
        )

        val result = manager.processRiskAssessment(assessment)
        assertNull(result)
        assertNull(manager.activeAlert.value)
    }

    @Test
    fun `processRiskAssessment with CRITICAL risk level should trigger alert and activeAlert`() {
        val manager = AlertManager()
        val assessment = RiskAssessmentResult(
            drowsinessProbability = 0.95f,
            rawRiskLevel = RiskLevel.CRITICAL,
            effectiveRiskLevel = RiskLevel.CRITICAL,
            consecutiveElevatedWindows = 2,
            dataQuality = dummyQuality
        )

        val alert = manager.processRiskAssessment(assessment, currentTimeMs = 1000L)
        assertNotNull(alert)
        assertEquals(RiskLevel.CRITICAL, alert?.riskLevel)
        assertEquals(0.95f, alert?.probability ?: 0f, 1e-4f)
        assertEquals(alert, manager.activeAlert.value)
    }

    @Test
    fun `processRiskAssessment within cooldown window should suppress repetitive alerts`() {
        val manager = AlertManager(
            config = AlertPolicyConfig(criticalCooldownMs = 30_000L)
        )
        val assessment = RiskAssessmentResult(
            drowsinessProbability = 0.92f,
            rawRiskLevel = RiskLevel.CRITICAL,
            effectiveRiskLevel = RiskLevel.CRITICAL,
            consecutiveElevatedWindows = 2,
            dataQuality = dummyQuality
        )

        // Primera alerta en t = 10,000ms -> Exitosa
        val firstAlert = manager.processRiskAssessment(assessment, currentTimeMs = 10_000L)
        assertNotNull(firstAlert)

        // Segunda alerta en t = 20,000ms (10s después, cooldown de 30s aún activo) -> Suprimida
        val suppressedAlert = manager.processRiskAssessment(assessment, currentTimeMs = 20_000L)
        assertNull(suppressedAlert)

        // Tercera alerta en t = 45,000ms (35s después, cooldown expirado) -> Emitida
        val renewedAlert = manager.processRiskAssessment(assessment, currentTimeMs = 45_000L)
        assertNotNull(renewedAlert)
    }

    @Test
    fun `acknowledging alert should clear active alert and record response latency`() {
        val manager = AlertManager()
        val assessment = RiskAssessmentResult(
            drowsinessProbability = 0.75f,
            rawRiskLevel = RiskLevel.HIGH,
            effectiveRiskLevel = RiskLevel.HIGH,
            consecutiveElevatedWindows = 2,
            dataQuality = dummyQuality
        )

        val alert = manager.processRiskAssessment(assessment, currentTimeMs = 1000L)
        assertNotNull(alert)

        // Reconocimiento 1500ms después
        val ackAlert = manager.acknowledgeActiveAlert(acknowledgedAtMs = 2500L)
        assertNotNull(ackAlert)
        assertTrue(ackAlert?.isAcknowledged == true)
        assertEquals(1500L, ackAlert?.responseLatencyMs)
        assertNull(manager.activeAlert.value)
    }
}
