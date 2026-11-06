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
            reactionTimeMs = null, // Debería imputar 350.0
            sessionDurationSeconds = 400.0f,
            hrvSdnnMs = null, // Debería imputar 45.0
            hrvRmssdMs = null // Debería imputar 40.0
        )

        val imputed = strategy.imputeVector(rawVector)

        assertEquals(8, imputed.size)
        assertEquals(65.0f, imputed[0], 1e-4f)
        assertEquals(9.81f, imputed[1], 1e-4f)
        assertEquals(0.05f, imputed[2], 1e-4f)
        assertEquals(12.0f, imputed[3], 1e-4f)
        assertEquals(350.0f, imputed[4], 1e-4f) // Imputado
        assertEquals(400.0f, imputed[5], 1e-4f)
        assertEquals(45.0f, imputed[6], 1e-4f)  // Imputado
        assertEquals(40.0f, imputed[7], 1e-4f)  // Imputado
    }

    @Test
    fun `training medians must contain exactly 8 default values`() {
        assertEquals(8, strategy.trainingMedians.size)
        val expectedMedians = floatArrayOf(72f, 9.81f, 0.05f, 10f, 350f, 600f, 45f, 40f)
        assertArrayEquals(expectedMedians, strategy.trainingMedians, 1e-4f)
    }
}
