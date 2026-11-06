package com.microalert.app.domain.usecase

import com.microalert.app.data.repository.SessionHistoryRepository

/**
 * Caso de uso para eliminar completamente el historial de sesiones y alertas locales.
 * Garantiza el cumplimiento del principio de privacidad por diseño y minimización de datos.
 */
class ClearHistoryUseCase(
    private val sessionHistoryRepository: SessionHistoryRepository
) {
    suspend operator fun invoke() {
        sessionHistoryRepository.clearAllHistory()
    }
}
