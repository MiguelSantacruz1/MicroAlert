package com.microalert.app.domain.model

/**
 * Modelo de dominio para representar una ventana procesada de señales fisiológicas y cinemáticas.
 */
data class SignalWindow(
    val timestampMs: Long,
    val heartRateBpm: Float?,
    val accelMagnitude: Float,
    val gyroMagnitude: Float,
    val inactivityDurationSeconds: Long,
    val hrvSdnnMs: Float?,
    val hrvRmssdMs: Float?
)
