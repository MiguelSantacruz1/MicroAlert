package com.microalert.app.signalprocessing

import org.junit.Assert.assertEquals
import org.junit.Test

class SignalFilterTest {

    @Test
    fun `moving average filter should smooth out noisy series`() {
        val filter = SignalFilter(windowSize = 3)

        val f1 = filter.filter(10f) // [10] -> 10.0
        val f2 = filter.filter(20f) // [10, 20] -> 15.0
        val f3 = filter.filter(30f) // [10, 20, 30] -> 20.0
        val f4 = filter.filter(40f) // [20, 30, 40] -> 30.0

        assertEquals(10f, f1, 1e-4f)
        assertEquals(15f, f2, 1e-4f)
        assertEquals(20f, f3, 1e-4f)
        assertEquals(30f, f4, 1e-4f)
    }
}
