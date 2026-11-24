package com.microalert.app.domain.classifier

/**
 * Calibración personal. Durante las primeras ventanas en reposo aprende el pulso base
 * y la probabilidad base del modelo de la persona. Si su línea base es más alta de lo
 * normal, sube los umbrales de riesgo para evitar falsas alarmas.
 */
class PersonalCalibrator(
    val windowsRequired: Int = 4,
    private val restProbabilityRef: Float = 0.05f,
    private val maxOffset: Float = 0.15f,
    private val restCeiling: Float = 0.30f
) {
    private var count = 0
    private var sumProbability = 0f
    private var sumHeartRate = 0f
    private var heartRateCount = 0

    val isCalibrated: Boolean get() = count >= windowsRequired
    val progress: Float get() = (count.toFloat() / windowsRequired).coerceAtMost(1f)

    var baselineHeartRate: Float? = null
        private set

    /** Cuánto se suben los umbrales (0 si la línea base es normal). */
    var thresholdOffset: Float = 0f
        private set

    /** Registra una ventana. Solo cuentan las ventanas en reposo (probabilidad baja). */
    fun observe(probability: Float, heartRateBpm: Float?) {
        if (isCalibrated || probability >= restCeiling) return
        count++
        sumProbability += probability
        if (heartRateBpm != null && heartRateBpm > 0f) {
            sumHeartRate += heartRateBpm
            heartRateCount++
        }
        if (isCalibrated) {
            val mean = sumProbability / count
            thresholdOffset = (mean - restProbabilityRef).coerceIn(0f, maxOffset)
            baselineHeartRate = if (heartRateCount > 0) sumHeartRate / heartRateCount else null
        }
    }

    fun reset() {
        count = 0
        sumProbability = 0f
        sumHeartRate = 0f
        heartRateCount = 0
        baselineHeartRate = null
        thresholdOffset = 0f
    }
}
