package com.microalert.app.utils

import org.junit.Assert.assertEquals
import org.junit.Test

class FormattersTest {

    @Test
    fun `formatDuration should format seconds, minutes and hours correctly`() {
        assertEquals("45s", Formatters.formatDuration(45L))
        assertEquals("1 min 30s", Formatters.formatDuration(90L))
        assertEquals("1h 05m", Formatters.formatDuration(3900L))
    }

    @Test
    fun `formatHeartRate should return formatted BPM or ND`() {
        assertEquals("75 BPM", Formatters.formatHeartRate(75.4f))
        assertEquals("N/D", Formatters.formatHeartRate(null))
        assertEquals("N/D", Formatters.formatHeartRate(0f))
    }
}
