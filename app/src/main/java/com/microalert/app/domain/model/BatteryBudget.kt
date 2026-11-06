package com.microalert.app.domain.model

/**
 * Parámetros y presupuesto de consumo energético para Wear OS.
 * Define frecuencias de muestreo y límites operativos recomendados.
 */
data class BatteryBudget(
    val maxHourlyDrainPercent: Float = 6.0f,
    val sensorSamplingPeriodUs: Int = 1_000_000, // 1 Hz para acelerómetro y giroscopio
    val heartRateSamplingPeriodUs: Int = 5_000_000, // 0.2 Hz para pulso óptico PPG
    val windowDurationSeconds: Int = 60,
    val windowStepSeconds: Int = 30,
    val isAggressiveDutyCyclingEnabled: Boolean = true
) {
    fun estimatedBatteryLifetimeHours(currentBatteryLevelPercent: Int): Float {
        return (currentBatteryLevelPercent / maxHourlyDrainPercent).coerceAtLeast(0f)
    }
}
