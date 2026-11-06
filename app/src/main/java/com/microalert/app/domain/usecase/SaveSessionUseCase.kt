package com.microalert.app.domain.usecase

import com.microalert.app.data.repository.SessionHistoryRepository
import com.microalert.app.domain.model.MonitoringSession

/**
 * Caso de uso para persistir una sesión de monitoreo finalizada en el almacenamiento local.
 */
class SaveSessionUseCase(
    private val sessionHistoryRepository: SessionHistoryRepository
) {
    suspend operator fun invoke(session: MonitoringSession): Long {
        return sessionHistoryRepository.saveSession(session)
    }
}
