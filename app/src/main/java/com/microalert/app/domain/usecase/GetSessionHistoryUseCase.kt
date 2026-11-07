package com.microalert.app.domain.usecase

import com.microalert.app.data.repository.SessionHistoryRepository
import com.microalert.app.domain.model.MonitoringSession
import kotlinx.coroutines.flow.Flow

class GetSessionHistoryUseCase(
    private val sessionHistoryRepository: SessionHistoryRepository
) {
    operator fun invoke(): Flow<List<MonitoringSession>> {
        return sessionHistoryRepository.getAllSessions()
    }
}
