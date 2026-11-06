package com.microalert.app.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.microalert.app.domain.model.AppMode
import com.microalert.app.domain.model.RiskLevel

/**
 * Entidad Room para registrar una sesión de monitoreo completada.
 */
@Entity(tableName = "monitoring_sessions")
data class MonitoringSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTimestampMs: Long,
    val endTimestampMs: Long,
    val durationSeconds: Long,
    val appMode: String, // "REAL" o "DEMO"
    val maxRiskLevel: String, // NORMAL, PRECAUTION, HIGH, CRITICAL
    val alertsTriggeredCount: Int,
    val avgHeartRateBpm: Float?,
    val processedWindowsCount: Int
)
