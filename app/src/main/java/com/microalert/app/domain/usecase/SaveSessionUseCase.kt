package com.microalert.app.domain.usecase

import com.microalert.app.data.repository.SessionHistoryRepository
import com.microalert.app.domain.model.MonitoringSession

class SaveSessionUseCase(
    private val sessionHistoryRepository: SessionHistoryRepository
) {
    suspend operator fun invoke(session: MonitoringSession): Long {
        return sessionHistoryRepository.saveSession(session)
    }
}
