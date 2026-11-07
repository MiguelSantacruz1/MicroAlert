package com.microalert.app.signalprocessing.model

data class ProcessedWindowData(
    val windowStartTimestampMs: Long,
    val windowEndTimestampMs: Long,
    val avgHeartRateBpm: Float?,
    val avgAccelerometerMagnitude: Float,
    val avgGyroscopeMagnitude: Float,
    val inactivitySeconds: Long,
    val hrvSdnnMs: Float?,
    val hrvRmssdMs: Float?,
    val sampleCount: Int,
    val isHrvAvailable: Boolean
)
