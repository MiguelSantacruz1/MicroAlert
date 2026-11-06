package com.microalert.app.data

import com.microalert.app.data.repository.SessionHistoryRepositoryImpl
import com.microalert.app.domain.model.AlertEvent
import com.microalert.app.domain.model.AppMode
import com.microalert.app.domain.model.MonitoringSession
import com.microalert.app.domain.model.RiskLevel
import com.microalert.app.storage.dao.AlertDao
import com.microalert.app.storage.dao.SessionDao
import com.microalert.app.storage.entity.AlertRecordEntity
import com.microalert.app.storage.entity.MonitoringSessionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SessionHistoryRepositoryTest {

    private lateinit var fakeSessionDao: FakeSessionDao
    private lateinit var fakeAlertDao: FakeAlertDao
    private lateinit var repository: SessionHistoryRepositoryImpl

    @Before
    fun setUp() {
        fakeSessionDao = FakeSessionDao()
        fakeAlertDao = FakeAlertDao()
        repository = SessionHistoryRepositoryImpl(fakeSessionDao, fakeAlertDao)
    }

    @Test
    fun `saveSession should insert into sessionDao and be observable in getAllSessions`() = runTest {
        val session = MonitoringSession(
            id = 1L,
            startTimestampMs = 1000L,
            endTimestampMs = 61000L,
            durationSeconds = 60L,
            appMode = AppMode.REAL,
            maxRiskLevel = RiskLevel.HIGH,
            alertsTriggeredCount = 2,
            avgHeartRateBpm = 75f,
            processedWindowsCount = 2
        )

        val insertedId = repository.saveSession(session)
        assertEquals(1L, insertedId)

        val list = repository.getAllSessions().first()
        assertEquals(1, list.size)
        val retrieved = list.first()
        assertEquals(RiskLevel.HIGH, retrieved.maxRiskLevel)
        assertEquals(60L, retrieved.durationSeconds)
        assertEquals(AppMode.REAL, retrieved.appMode)
    }

    @Test
    fun `recordAlertForSession should insert into alertDao`() = runTest {
        val alert = AlertEvent(
            id = "alert-10",
            timestampMs = 5000L,
            riskLevel = RiskLevel.CRITICAL,
            probability = 0.95f,
            message = "Alerta Crítica",
            isAcknowledged = false,
            responseLatencyMs = null
        )

        val alertId = repository.recordAlertForSession(1L, alert)
        assertEquals(1L, alertId)
        assertEquals(1, fakeAlertDao.alerts.size)
        assertEquals("CRITICAL", fakeAlertDao.alerts.first().riskLevel)
    }

    @Test
    fun `clearAllHistory should clear both sessions and alerts`() = runTest {
        repository.saveSession(
            MonitoringSession(
                id = 1L,
                startTimestampMs = 100L,
                endTimestampMs = 200L,
                durationSeconds = 10L,
                appMode = AppMode.REAL,
                maxRiskLevel = RiskLevel.NORMAL,
                alertsTriggeredCount = 0,
                avgHeartRateBpm = 70f,
                processedWindowsCount = 1
            )
        )
        repository.recordAlertForSession(
            1L,
            AlertEvent(
                id = "alert-1",
                timestampMs = 150L,
                riskLevel = RiskLevel.PRECAUTION,
                probability = 0.5f,
                message = "Alerta de precaución"
            )
        )

        assertEquals(1, repository.getSessionCount())
        assertEquals(1, fakeAlertDao.alerts.size)

        repository.clearAllHistory()

        assertEquals(0, repository.getSessionCount())
        assertEquals(0, fakeAlertDao.alerts.size)
        assertTrue(repository.getAllSessions().first().isEmpty())
    }

    private class FakeSessionDao : SessionDao {
        private val sessions = mutableListOf<MonitoringSessionEntity>()
        private val sessionsFlow = MutableStateFlow<List<MonitoringSessionEntity>>(emptyList())

        override fun getAllSessions(): Flow<List<MonitoringSessionEntity>> = sessionsFlow

        override suspend fun insertSession(session: MonitoringSessionEntity): Long {
            sessions.add(session)
            sessionsFlow.value = sessions.toList()
            return session.id
        }

        override suspend fun getSessionById(sessionId: Long): MonitoringSessionEntity? {
            return sessions.find { it.id == sessionId }
        }

        override suspend fun deleteSessionById(sessionId: Long) {
            sessions.removeAll { it.id == sessionId }
            sessionsFlow.value = sessions.toList()
        }

        override suspend fun clearAllSessions() {
            sessions.clear()
            sessionsFlow.value = emptyList()
        }

        override suspend fun getSessionCount(): Int = sessions.size
    }

    private class FakeAlertDao : AlertDao {
        val alerts = mutableListOf<AlertRecordEntity>()

        override fun getAlertsForSession(sessionId: Long): Flow<List<AlertRecordEntity>> {
            return MutableStateFlow(alerts.filter { it.sessionId == sessionId })
        }

        override fun getRecentAlerts(limit: Int): Flow<List<AlertRecordEntity>> {
            return MutableStateFlow(alerts.takeLast(limit))
        }

        override suspend fun deleteAlertsForSession(sessionId: Long) {
            alerts.removeAll { it.sessionId == sessionId }
        }

        override suspend fun insertAlert(alert: AlertRecordEntity): Long {
            val generatedId = (alerts.size + 1).toLong()
            alerts.add(alert.copy(id = generatedId))
            return generatedId
        }

        override suspend fun clearAllAlerts() {
            alerts.clear()
        }
    }
}
