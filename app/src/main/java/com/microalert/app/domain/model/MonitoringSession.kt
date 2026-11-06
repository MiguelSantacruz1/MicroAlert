package com.microalert.app.domain.model

/**
 * Modelo de dominio para representar una sesión de monitoreo.
 */
data class MonitoringSession(
    val id: Long = 0,
    val startTimestampMs: Long,
    val endTimestampMs: Long,
    val durationSeconds: Long,
    val appMode: AppMode,
    val maxRiskLevel: RiskLevel,
    val alertsTriggeredCount: Int,
    val avgHeartRateBpm: Float?,
    val processedWindowsCount: Int
)
