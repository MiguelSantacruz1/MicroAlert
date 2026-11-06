package com.microalert.app.utils

import org.junit.Assert.assertNotNull
import org.junit.Test

class CrashLoggerTest {

    @Test
    fun `crash logger instance should be accessible`() {
        assertNotNull(CrashLogger)
    }
}
