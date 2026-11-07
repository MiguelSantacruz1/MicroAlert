package com.microalert.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test

class FeatureVectorTest {

    private val defaultMedians = floatArrayOf(
        72.0f, 9.81f, 0.05f, 10.0f, 350.0f, 600.0f, 45.0f, 40.0f
    )

    @Test
    fun `complete feature vector should maintain exact order of 8 variables`() {
        val vector = FeatureVector(
            heartRate = 68.0f,
            accelerometerMagnitude = 9.85f,
            gyroscopeMagnitude = 0.03f,
            inactivityDurationSeconds = 25.0f,
            reactionTimeMs = 310.0f,
            sessionDurationSeconds = 180.0f,
            hrvSdnnMs = 50.0f,
            hrvRmssdMs = 42.0f
        )

        val array = vector.toFloatArray(defaultMedians)

        assertEquals(8, array.size)
        assertEquals(68.0f, array[0], 1e-4f)
        assertEquals(9.85f, array[1], 1e-4f)
        assertEquals(0.03f, array[2], 1e-4f)
        assertEquals(25.0f, array[3], 1e-4f)
        assertEquals(310.0f, array[4], 1e-4f)
        assertEquals(180.0f, array[5], 1e-4f)
        assertEquals(50.0f, array[6], 1e-4f)
        assertEquals(42.0f, array[7], 1e-4f)
    }

    @Test
    fun `feature vector with missing values should fallback to training medians`() {
        val vectorWithNulls = FeatureVector(
            heartRate = null,
            accelerometerMagnitude = 9.80f,
            gyroscopeMagnitude = 0.04f,
            inactivityDurationSeconds = 15.0f,
            reactionTimeMs = null,
            sessionDurationSeconds = 300.0f,
            hrvSdnnMs = null,
            hrvRmssdMs = null
        )

        val array = vectorWithNulls.toFloatArray(defaultMedians)

        assertEquals(8, array.size)
        assertEquals(defaultMedians[0], array[0], 1e-4f)
        assertEquals(9.80f, array[1], 1e-4f)
        assertEquals(0.04f, array[2], 1e-4f)
        assertEquals(15.0f, array[3], 1e-4f)
        assertEquals(defaultMedians[4], array[4], 1e-4f)
        assertEquals(300.0f, array[5], 1e-4f)
        assertEquals(defaultMedians[6], array[6], 1e-4f)
        assertEquals(defaultMedians[7], array[7], 1e-4f)
    }
}
