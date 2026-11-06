package com.microalert.app.signalprocessing

import com.microalert.app.sensors.model.SensorSample
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class FeatureAggregatorTest {

    private val aggregator = FeatureAggregator()

    @Test
    fun `aggregating empty samples list should return default empty window`() {
        val window = aggregator.aggregateWindow(emptyList())
        assertEquals(0, window.sampleCount)
        assertEquals(0f, window.avgAccelerometerMagnitude, 1e-4f)
    }

    @Test
    fun `aggregating valid samples list should calculate correct averages`() {
        val samples = listOf(
            SensorSample(heartRateBpm = 70f, accelMagnitude = 9.8f, gyroMagnitude = 0.1f),
            SensorSample(heartRateBpm = 80f, accelMagnitude = 10.0f, gyroMagnitude = 0.3f)
        )

        val window = aggregator.aggregateWindow(samples)
        assertEquals(2, window.sampleCount)
        assertNotNull(window.avgHeartRateBpm)
        assertEquals(75f, window.avgHeartRateBpm!!, 1e-4f)
        assertEquals(9.9f, window.avgAccelerometerMagnitude, 1e-4f)
        assertEquals(0.2f, window.avgGyroscopeMagnitude, 1e-4f)
    }
}
