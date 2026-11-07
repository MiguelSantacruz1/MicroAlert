package com.microalert.app.domain.model

data class AlertEvent(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestampMs: Long = System.currentTimeMillis(),
    val riskLevel: RiskLevel,
    val probability: Float,
    val message: String,
    val isAcknowledged: Boolean = false,
    val acknowledgedTimestampMs: Long? = null,
    val responseLatencyMs: Long? = null
) {
    fun acknowledge(acknowledgedAtMs: Long = System.currentTimeMillis()): AlertEvent {
        val latency = acknowledgedAtMs - timestampMs
        return copy(
            isAcknowledged = true,
            acknowledgedTimestampMs = acknowledgedAtMs,
            responseLatencyMs = latency
        )
    }
}
