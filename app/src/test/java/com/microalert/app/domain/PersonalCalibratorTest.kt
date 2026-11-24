package com.microalert.app.domain

import com.microalert.app.domain.classifier.PersonalCalibrator
import com.microalert.app.domain.classifier.RiskPolicyManager
import com.microalert.app.domain.model.RiskLevel
import com.microalert.app.domain.model.WindowDataQuality
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PersonalCalibratorTest {

    @Test
    fun `calibra tras las ventanas requeridas y guarda el pulso base`() {
        val c = PersonalCalibrator(windowsRequired = 3)
        repeat(2) { c.observe(0.02f, 70f) }
        assertFalse(c.isCalibrated)
        c.observe(0.02f, 76f)
        assertTrue(c.isCalibrated)
        assertEquals(72f, c.baselineHeartRate!!, 0.01f)
        assertEquals(0f, c.thresholdOffset, 0.001f)
    }

    @Test
    fun `ignora ventanas con riesgo alto al calibrar`() {
        val c = PersonalCalibrator(windowsRequired = 2)
        repeat(5) { c.observe(0.9f, 60f) }
        assertFalse(c.isCalibrated)
    }

    @Test
    fun `linea base alta sube el umbral y evita la falsa alarma`() {
        val c = PersonalCalibrator(windowsRequired = 3)
        repeat(3) { c.observe(0.20f, 72f) }
        assertEquals(0.15f, c.thresholdOffset, 0.001f)

        val quality = WindowDataQuality(true, true, true, true, true, true, true, true)
        val factory = RiskPolicyManager()
        val personal = RiskPolicyManager().also { it.thresholdOffset = c.thresholdOffset }
        assertEquals(RiskLevel.PRECAUTION, factory.evaluateProbability(0.35f, quality).effectiveRiskLevel)
        assertEquals(RiskLevel.NORMAL, personal.evaluateProbability(0.35f, quality).effectiveRiskLevel)
    }
}
