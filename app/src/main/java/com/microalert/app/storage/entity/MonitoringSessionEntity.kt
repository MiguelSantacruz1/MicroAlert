package com.microalert.app.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.microalert.app.domain.model.AppMode
import com.microalert.app.domain.model.RiskLevel

@Entity(tableName = "monitoring_sessions")
data class MonitoringSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTimestampMs: Long,
    val endTimestampMs: Long,
    val durationSeconds: Long,
    val appMode: String,
    val maxRiskLevel: String,
    val alertsTriggeredCount: Int,
    val avgHeartRateBpm: Float?,
    val processedWindowsCount: Int
)
