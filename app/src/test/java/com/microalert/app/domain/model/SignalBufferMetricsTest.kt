package com.microalert.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class SignalBufferMetricsTest {

    @Test
    fun `dropRatePercent should calculate accurate percentage`() {
        val metrics = SignalBufferMetrics(
            totalSamplesReceived = 1000L,
            totalWindowsEmitted = 30L,
            droppedSamplesOutOfOrder = 5L,
            currentBufferCapacity = 60
        )

        assertEquals(0.5f, metrics.dropRatePercent, 0.01f)
    }
}
