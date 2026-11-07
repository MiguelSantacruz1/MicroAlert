package com.microalert.app.storage.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "alert_records",
    foreignKeys = [
        ForeignKey(
            entity = MonitoringSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class AlertRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val timestampMs: Long,
    val riskLevel: String,
    val probability: Float,
    val message: String,
    val isAcknowledged: Boolean,
    val responseLatencyMs: Long?
)
