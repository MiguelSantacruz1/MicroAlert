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
        assertNotNull(result1) // Primera emisión inicial

        val sample2 = SensorSample(timestampMs = 10_000L, heartRateBpm = 72f)
        val result2 = manager.addSample(sample2)
        assertNull(result2) // No han pasado 30s aún

        val sample3 = SensorSample(timestampMs = 31_000L, heartRateBpm = 75f)
        val result3 = manager.addSample(sample3)
        assertNotNull(result3) // Pasaron > 30s desde la primera emisión
        assertEquals(3, result3?.size)
    }
}
