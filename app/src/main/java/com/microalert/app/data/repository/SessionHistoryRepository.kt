package com.microalert.app.data.repository

import com.microalert.app.domain.model.AlertEvent
import com.microalert.app.domain.model.MonitoringSession
import kotlinx.coroutines.flow.Flow

interface SessionHistoryRepository {
    fun getAllSessions(): Flow<List<MonitoringSession>>
    suspend fun saveSession(session: MonitoringSession): Long
    suspend fun recordAlertForSession(sessionId: Long, alert: AlertEvent): Long
    suspend fun deleteSession(sessionId: Long)
    suspend fun clearAllHistory()
    suspend fun getSessionCount(): Int
}
