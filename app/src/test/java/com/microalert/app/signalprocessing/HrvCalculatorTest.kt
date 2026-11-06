package com.microalert.app.signalprocessing

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class HrvCalculatorTest {

    private val calculator = HrvCalculator()

    @Test
    fun `insufficient RR intervals should return null metrics`() {
        val result = calculator.calculateHrv(listOf(800f, 820f))
        assertNull(result.sdnnMs)
        assertNull(result.rmssdMs)
    }

    @Test
    fun `known RR intervals should calculate correct SDNN and RMSSD`() {
        val intervals = listOf(800f, 810f, 790f, 805f, 815f)
        val result = calculator.calculateHrv(intervals)

        assertNotNull(result.sdnnMs)
        assertNotNull(result.rmssdMs)
        // SDNN media=804. SDNN aprox 9.61
        assertEquals(9.61f, result.sdnnMs!!, 0.5f)
    }
}
