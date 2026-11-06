package com.microalert.app.domain.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ClinicalDisclaimerTest {

    @Test
    fun `disclaimer must contain non-medical device warning`() {
        assertTrue(ClinicalDisclaimer.SHORT_DISCLAIMER.contains("No es un dispositivo médico"))
        assertFalse(ClinicalDisclaimer.FULL_DISCLAIMER.isEmpty())
    }
}
