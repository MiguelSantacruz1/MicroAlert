package com.microalert.app.data.repository

import com.microalert.app.domain.model.AlertEvent
import com.microalert.app.domain.model.AppMode
import com.microalert.app.domain.model.MonitoringSession
import com.microalert.app.domain.model.RiskLevel
import com.microalert.app.storage.dao.AlertDao
import com.microalert.app.storage.dao.SessionDao
import com.microalert.app.storage.entity.AlertRecordEntity
import com.microalert.app.storage.entity.MonitoringSessionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementación de SessionHistoryRepository utilizando Room DAOs.
 */
class SessionHistoryRepositoryImpl(
    private val sessionDao: SessionDao,
    private val alertDao: AlertDao
) : SessionHistoryRepository {

    override fun getAllSessions(): Flow<List<MonitoringSession>> {
        return sessionDao.getAllSessions().map { entities ->
            entities.map { entity ->
                MonitoringSession(
                    id = entity.id,
                    startTimestampMs = entity.startTimestampMs,
                    endTimestampMs = entity.endTimestampMs,
                    durationSeconds = entity.durationSeconds,
                    appMode = if (entity.appMode == AppMode.DEMO.name) AppMode.DEMO else AppMode.REAL,
                    maxRiskLevel = try {
                        RiskLevel.valueOf(entity.maxRiskLevel)
                    } catch (e: Exception) {
                        RiskLevel.NORMAL
                    },
                    alertsTriggeredCount = entity.alertsTriggeredCount,
                    avgHeartRateBpm = entity.avgHeartRateBpm,
                    processedWindowsCount = entity.processedWindowsCount
                )
            }
        }
    }

    override suspend fun saveSession(session: MonitoringSession): Long {
        val entity = MonitoringSessionEntity(
            id = session.id,
            startTimestampMs = session.startTimestampMs,
            endTimestampMs = session.endTimestampMs,
            durationSeconds = session.durationSeconds,
            appMode = session.appMode.name,
            maxRiskLevel = session.maxRiskLevel.name,
            alertsTriggeredCount = session.alertsTriggeredCount,
            avgHeartRateBpm = session.avgHeartRateBpm,
            processedWindowsCount = session.processedWindowsCount
        )
        return sessionDao.insertSession(entity)
    }

    override suspend fun recordAlertForSession(sessionId: Long, alert: AlertEvent): Long {
        val entity = AlertRecordEntity(
            sessionId = sessionId,
            timestampMs = alert.timestampMs,
            riskLevel = alert.riskLevel.name,
            probability = alert.probability,
            message = alert.message,
            isAcknowledged = alert.isAcknowledged,
            responseLatencyMs = alert.responseLatencyMs
        )
        return alertDao.insertAlert(entity)
    }

    override suspend fun deleteSession(sessionId: Long) {
        sessionDao.deleteSessionById(sessionId)
    }

    override suspend fun clearAllHistory() {
        alertDao.clearAllAlerts()
        sessionDao.clearAllSessions()
    }

    override suspend fun getSessionCount(): Int {
        return sessionDao.getSessionCount()
    }
}
