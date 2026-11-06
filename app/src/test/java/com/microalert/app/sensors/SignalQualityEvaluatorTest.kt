package com.microalert.app.sensors

import com.microalert.app.sensors.model.SensorSample
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SignalQualityEvaluatorTest {

    private val evaluator = SignalQualityEvaluator()

    @Test
    fun `valid BPM between 30 and 220 should pass`() {
        assertTrue(evaluator.isHeartRateValid(75.0f))
        assertTrue(evaluator.isHeartRateValid(40.0f))
        assertTrue(evaluator.isHeartRateValid(190.0f))
    }

    @Test
    fun `invalid BPM below 30 or above 220 should fail`() {
        assertFalse(evaluator.isHeartRateValid(15.0f))
        assertFalse(evaluator.isHeartRateValid(250.0f))
        assertFalse(evaluator.isHeartRateValid(null))
    }

    @Test
    fun `evaluateSample should filter out invalid heart rate`() {
        val sampleWithExtremeHr = SensorSample(
            heartRateBpm = 300.0f,
            accelMagnitude = 9.81f,
            gyroMagnitude = 0.05f
        )
        val filtered = evaluator.evaluateSample(sampleWithExtremeHr)

        assertNull(filtered.heartRateBpm)
        assertFalse(filtered.isHeartRateValid)
        assertTrue(filtered.isAccelValid)
        assertTrue(filtered.isGyroValid)
    }
}
