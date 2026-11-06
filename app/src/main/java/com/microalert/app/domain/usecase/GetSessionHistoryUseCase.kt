package com.microalert.app.domain.usecase

import com.microalert.app.data.repository.SessionHistoryRepository
import com.microalert.app.domain.model.MonitoringSession
import kotlinx.coroutines.flow.Flow

/**
 * Caso de uso para obtener el flujo reactivo de sesiones registradas en el historial.
 */
class GetSessionHistoryUseCase(
    private val sessionHistoryRepository: SessionHistoryRepository
) {
    operator fun invoke(): Flow<List<MonitoringSession>> {
        return sessionHistoryRepository.getAllSessions()
    }
}
