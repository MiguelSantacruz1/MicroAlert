package com.microalert.app.domain.model

enum class SignalQualityLevel(val label: String) {
    EXCELLENT("Excelente"),
    ACCEPTABLE("Aceptable"),
    DEGRADED("Degradada"),
    UNAVAILABLE("No Disponible")
}

data class SignalQualitySummary(
    val heartRateQuality: SignalQualityLevel,
    val motionQuality: SignalQualityLevel,
    val hrvQuality: SignalQualityLevel,
    val isReadyForInference: Boolean
)
