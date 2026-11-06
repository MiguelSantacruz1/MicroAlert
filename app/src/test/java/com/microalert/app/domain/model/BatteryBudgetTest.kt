package com.microalert.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BatteryBudgetTest {

    @Test
    fun `estimatedBatteryLifetimeHours should calculate hours based on hourly drain`() {
        val budget = BatteryBudget(maxHourlyDrainPercent = 5.0f)
        val lifetime = budget.estimatedBatteryLifetimeHours(100)
        assertEquals(20.0f, lifetime, 0.01f)

        assertTrue(budget.sensorSamplingPeriodUs >= 1_000_000)
    }
}
