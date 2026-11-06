package com.microalert.app.sensors

import android.Manifest
import org.junit.Assert.assertTrue
import org.junit.Test

class PermissionManagerTest {

    @Test
    fun `required permissions should include body sensors and activity recognition`() {
        val required = listOf(
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
        assertTrue(required.contains(Manifest.permission.BODY_SENSORS))
        assertTrue(required.contains(Manifest.permission.ACTIVITY_RECOGNITION))
    }
}
