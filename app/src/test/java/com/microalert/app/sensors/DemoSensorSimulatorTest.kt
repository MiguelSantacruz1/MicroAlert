package com.microalert.app.sensors

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.sqrt

class DemoSensorSimulatorTest {

    @Test
    fun `isAvailable methods should return true for DEMO mode`() {
        val simulator = DemoSensorSimulator()
        assertTrue(simulator.isHeartRateAvailable())
        assertTrue(simulator.isAccelerometerAvailable())
        assertTrue(simulator.isGyroscopeAvailable())
        assertTrue(simulator.isHrvAvailable())
    }

    @Test
    fun `vector magnitude calculation formula verification`() {
        val x = 3.0f
        val y = 4.0f
        val z = 0.0f
        val expectedMagnitude = 5.0f
        val actualMagnitude = sqrt(x * x + y * y + z * z)
        assertEquals(expectedMagnitude, actualMagnitude, 1e-4f)
    }
}
