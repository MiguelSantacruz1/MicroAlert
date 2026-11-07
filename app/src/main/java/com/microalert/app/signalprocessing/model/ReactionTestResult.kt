package com.microalert.app.signalprocessing.model

data class ReactionTestResult(
    val timestampMs: Long = System.currentTimeMillis(),
    val latencyMs: Long?,
    val isSuccessful: Boolean,
    val isTimedOut: Boolean,
    val totalAttempts: Int = 1
)
