package com.microalert.app.domain.model

data class SignalWindow(
    val timestampMs: Long,
    val heartRateBpm: Float?,
    val accelMagnitude: Float,
    val gyroMagnitude: Float,
    val inactivityDurationSeconds: Long,
    val hrvSdnnMs: Float?,
    val hrvRmssdMs: Float?
)
