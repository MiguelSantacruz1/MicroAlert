package com.microalert.app.domain.model

data class BatteryBudget(
    val maxHourlyDrainPercent: Float = 6.0f,
    val sensorSamplingPeriodUs: Int = 1_000_000,
    val heartRateSamplingPeriodUs: Int = 5_000_000,
    val windowDurationSeconds: Int = 60,
    val windowStepSeconds: Int = 30,
    val isAggressiveDutyCyclingEnabled: Boolean = true
) {
    fun estimatedBatteryLifetimeHours(currentBatteryLevelPercent: Int): Float {
        return (currentBatteryLevelPercent / maxHourlyDrainPercent).coerceAtLeast(0f)
    }
}
