package com.microalert.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SignalQualityTest {

    @Test
    fun `signal quality summary should correctly indicate readiness`() {
        val summary = SignalQualitySummary(
            heartRateQuality = SignalQualityLevel.EXCELLENT,
            motionQuality = SignalQualityLevel.ACCEPTABLE,
            hrvQuality = SignalQualityLevel.UNAVAILABLE,
            isReadyForInference = true
        )

        assertTrue(summary.isReadyForInference)
        assertEquals("Excelente", summary.heartRateQuality.label)
        assertEquals("No Disponible", summary.hrvQuality.label)
    }
}
