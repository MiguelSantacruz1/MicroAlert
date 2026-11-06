package com.microalert.app.domain.usecase

import com.microalert.app.data.repository.SessionHistoryRepository
import com.microalert.app.domain.model.AlertEvent
import com.microalert.app.domain.model.AppMode
import com.microalert.app.domain.model.MonitoringSession
import com.microalert.app.domain.model.RiskLevel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SessionHistoryUseCasesTest {

    private lateinit var fakeRepository: FakeSessionHistoryRepository
    private lateinit var saveSessionUseCase: SaveSessionUseCase
    private lateinit var getSessionHistoryUseCase: GetSessionHistoryUseCase
    private lateinit var clearHistoryUseCase: ClearHistoryUseCase

    @Before
    fun setUp() {
        fakeRepository = FakeSessionHistoryRepository()
        saveSessionUseCase = SaveSessionUseCase(fakeRepository)
        getSessionHistoryUseCase = GetSessionHistoryUseCase(fakeRepository)
        clearHistoryUseCase = ClearHistoryUseCase(fakeRepository)
    }

    @Test
    fun `save and get session history should store and observe records`() = runTest {
        val session = MonitoringSession(
            id = 42L,
            startTimestampMs = 1000L,
            endTimestampMs = 121000L,
            durationSeconds = 120L,
            appMode = AppMode.REAL,
            maxRiskLevel = RiskLevel.CRITICAL,
            alertsTriggeredCount = 1,
            avgHeartRateBpm = 68f,
            processedWindowsCount = 4
        )

        val id = saveSessionUseCase(session)
        assertEquals(42L, id)

        val list = getSessionHistoryUseCase().first()
        assertEquals(1, list.size)
        assertEquals(RiskLevel.CRITICAL, list.first().maxRiskLevel)
    }

    @Test
    fun `clearHistoryUseCase should erase all session data for privacy compliance`() = runTest {
        saveSessionUseCase(
            MonitoringSession(
                id = 1L,
                startTimestampMs = 10L,
                endTimestampMs = 20L,
                durationSeconds = 10L,
                appMode = AppMode.REAL,
                maxRiskLevel = RiskLevel.NORMAL,
                alertsTriggeredCount = 0,
                avgHeartRateBpm = 70f,
                processedWindowsCount = 1
            )
        )
        assertEquals(1, getSessionHistoryUseCase().first().size)

        clearHistoryUseCase()
        assertTrue(getSessionHistoryUseCase().first().isEmpty())
    }

    private class FakeSessionHistoryRepository : SessionHistoryRepository {
        private val sessions = mutableListOf<MonitoringSession>()
        private val flow = MutableStateFlow<List<MonitoringSession>>(emptyList())

        override fun getAllSessions(): Flow<List<MonitoringSession>> = flow

        override suspend fun saveSession(session: MonitoringSession): Long {
            sessions.add(session)
            flow.value = sessions.toList()
            return session.id
        }

        override suspend fun recordAlertForSession(sessionId: Long, alert: AlertEvent): Long = 1L

        override suspend fun deleteSession(sessionId: Long) {
            sessions.removeAll { it.id == sessionId }
            flow.value = sessions.toList()
        }

        override suspend fun clearAllHistory() {
            sessions.clear()
            flow.value = emptyList()
        }

        override suspend fun getSessionCount(): Int = sessions.size
    }
}
