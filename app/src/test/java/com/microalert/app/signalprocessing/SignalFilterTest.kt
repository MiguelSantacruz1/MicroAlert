package com.microalert.app.signalprocessing

import org.junit.Assert.assertEquals
import org.junit.Test

class SignalFilterTest {

    @Test
    fun `moving average filter should smooth out noisy series`() {
        val filter = SignalFilter(windowSize = 3)

        val f1 = filter.filter(10f)
        val f2 = filter.filter(20f)
        val f3 = filter.filter(30f)
        val f4 = filter.filter(40f)

        assertEquals(10f, f1, 1e-4f)
        assertEquals(15f, f2, 1e-4f)
        assertEquals(20f, f3, 1e-4f)
        assertEquals(30f, f4, 1e-4f)
    }
}
