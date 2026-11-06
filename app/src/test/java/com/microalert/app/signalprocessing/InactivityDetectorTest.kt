package com.microalert.app.signalprocessing

import com.microalert.app.sensors.model.SensorSample
import org.junit.Assert.assertEquals
import org.junit.Test

class InactivityDetectorTest {

    private val detector = InactivityDetector(movementThreshold = 0.35f)

    @Test
    fun `static gravity acceleration samples should count as inactive time`() {
        val staticSamples = List(10) {
            SensorSample(accelMagnitude = 9.81f)
        }

        val seconds = detector.calculateInactivityDurationSeconds(staticSamples)
        assertEquals(5L, seconds) // 10 muestras * 0.5s = 5s
    }

    @Test
    fun `high acceleration movement samples should not count as inactive time`() {
        val activeSamples = List(10) {
            SensorSample(accelMagnitude = 15.0f)
        }

        val seconds = detector.calculateInactivityDurationSeconds(activeSamples)
        assertEquals(0L, seconds)
    }
}
