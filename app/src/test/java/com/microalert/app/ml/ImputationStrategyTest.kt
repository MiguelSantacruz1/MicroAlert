package com.microalert.app.ml

import com.microalert.app.domain.model.FeatureVector
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class ImputationStrategyTest {

    private val strategy = ImputationStrategy()

    @Test
    fun `imputeVector should replace nulls with training medians without modifying valid features`() {
        val rawVector = FeatureVector(
            heartRate = 65.0f,
            accelerometerMagnitude = 9.81f,
            gyroscopeMagnitude = 0.05f,
            inactivityDurationSeconds = 12.0f,
            reactionTimeMs = null,
            sessionDurationSeconds = 400.0f,
            hrvSdnnMs = null,
            hrvRmssdMs = null
        )

        val imputed = strategy.imputeVector(rawVector)

        assertEquals(8, imputed.size)
        assertEquals(65.0f, imputed[0], 1e-4f)
        assertEquals(9.81f, imputed[1], 1e-4f)
        assertEquals(0.05f, imputed[2], 1e-4f)
        assertEquals(12.0f, imputed[3], 1e-4f)
        assertEquals(357.55f, imputed[4], 1e-4f)
        assertEquals(400.0f, imputed[5], 1e-4f)
        assertEquals(49.46f, imputed[6], 1e-4f)
        assertEquals(44.60f, imputed[7], 1e-4f)
    }

    @Test
    fun `training medians must contain exactly 8 default values`() {
        assertEquals(8, strategy.trainingMedians.size)
        val expectedMedians = floatArrayOf(70.45f, 9.81f, 0.04f, 17.80f, 357.55f, 915.0f, 49.46f, 44.60f)
        assertArrayEquals(expectedMedians, strategy.trainingMedians, 1e-4f)
    }
}
