package com.microalert.app.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SignalProcessingConfigTest {

    @Test
    fun `isHeartRatePlausible should validate physiological boundaries`() {
        val config = SignalProcessingConfig()
        assertTrue(config.isHeartRatePlausible(72f))
        assertFalse(config.isHeartRatePlausible(15f))
        assertFalse(config.isHeartRatePlausible(250f))
    }

    @Test
    fun `isRrIntervalPlausible should validate RR interval ranges`() {
        val config = SignalProcessingConfig()
        assertTrue(config.isRrIntervalPlausible(800f))
        assertFalse(config.isRrIntervalPlausible(100f))
        assertFalse(config.isRrIntervalPlausible(3000f))
    }
}
