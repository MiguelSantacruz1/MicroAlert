package com.microalert.app.domain.model

/**
 * Parámetros configurables del motor de procesamiento de señales en tiempo real.
 */
data class SignalProcessingConfig(
    val windowDurationMs: Long = 60_000L,
    val windowStepMs: Long = 30_000L,
    val inactivityAccelThreshold: Float = 0.35f,
    val minValidHeartRateBpm: Float = 35.0f,
    val maxValidHeartRateBpm: Float = 220.0f,
    val minValidRrIntervalMs: Float = 272.0f, // Corresponde a 220 BPM
    val maxValidRrIntervalMs: Float = 2000.0f  // Corresponde a 30 BPM
) {
    fun isHeartRatePlausible(bpm: Float): Boolean {
        return bpm in minValidHeartRateBpm..maxValidHeartRateBpm
    }

    fun isRrIntervalPlausible(rrMs: Float): Boolean {
        return rrMs in minValidRrIntervalMs..maxValidRrIntervalMs
    }
}
