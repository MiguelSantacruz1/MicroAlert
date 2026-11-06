package com.microalert.app.signalprocessing.model

/**
 * Registro del resultado de una prueba de reacción táctil/visual.
 */
data class ReactionTestResult(
    val timestampMs: Long = System.currentTimeMillis(),
    val latencyMs: Long?,
    val isSuccessful: Boolean,
    val isTimedOut: Boolean,
    val totalAttempts: Int = 1
)
