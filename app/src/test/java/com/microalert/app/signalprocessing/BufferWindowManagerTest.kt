package com.microalert.app.signalprocessing

import com.microalert.app.sensors.model.SensorSample
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class BufferWindowManagerTest {

    @Test
    fun `buffer manager should emit window only after step duration`() {
        val manager = BufferWindowManager(windowDurationMs = 60_000L, windowStepMs = 30_000L)

        val sample1 = SensorSample(timestampMs = 1000L, heartRateBpm = 70f)
        val result1 = manager.addSample(sample1)
        assertNull(result1) // No emite inmediatamente en la primera muestra

        val sample2 = SensorSample(timestampMs = 10_000L, heartRateBpm = 72f)
        val result2 = manager.addSample(sample2)
        assertNull(result2) // No han pasado 30s aún

        val sample3 = SensorSample(timestampMs = 31_000L, heartRateBpm = 75f)
        val result3 = manager.addSample(sample3)
        assertNotNull(result3) // Pasaron >= 30s desde el inicio
        assertEquals(3, result3?.size)
    }

    @Test
    fun `buffer manager should allow immediate emission if emitOnFirstSample is true`() {
        val manager = BufferWindowManager(
            windowDurationMs = 60_000L,
            windowStepMs = 30_000L,
            emitOnFirstSample = true
        )

        val sample1 = SensorSample(timestampMs = 1000L, heartRateBpm = 70f)
        val result1 = manager.addSample(sample1)
        assertNotNull(result1)
        assertEquals(1, result1?.size)
    }
}
