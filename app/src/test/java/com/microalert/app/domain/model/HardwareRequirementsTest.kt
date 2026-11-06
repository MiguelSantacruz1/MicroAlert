package com.microalert.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HardwareRequirementsTest {

    @Test
    fun `required sensors must include motion and heart rate sensors`() {
        assertEquals(3, HardwareRequirements.REQUIRED_SENSORS.size)
        assertTrue(HardwareRequirements.REQUIRED_SENSORS.contains("android.sensor.accelerometer"))
        assertTrue(HardwareRequirements.REQUIRED_SENSORS.contains("android.sensor.gyroscope"))
        assertTrue(HardwareRequirements.REQUIRED_SENSORS.contains("android.sensor.heart_rate"))
    }
}
