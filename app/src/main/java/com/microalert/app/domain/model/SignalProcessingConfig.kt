package com.microalert.app.domain.model

data class SignalProcessingConfig(
    val windowDurationMs: Long = 60_000L,
    val windowStepMs: Long = 30_000L,
    val inactivityAccelThreshold: Float = 0.35f,
    val minValidHeartRateBpm: Float = 35.0f,
    val maxValidHeartRateBpm: Float = 220.0f,
    val minValidRrIntervalMs: Float = 272.0f,
    val maxValidRrIntervalMs: Float = 2000.0f
) {
    fun isHeartRatePlausible(bpm: Float): Boolean {
        return bpm in minValidHeartRateBpm..maxValidHeartRateBpm
    }

    fun isRrIntervalPlausible(rrMs: Float): Boolean {
        return rrMs in minValidRrIntervalMs..maxValidRrIntervalMs
    }
}
