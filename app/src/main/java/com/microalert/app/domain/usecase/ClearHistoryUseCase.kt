package com.microalert.app.domain.usecase

import com.microalert.app.data.repository.SessionHistoryRepository

class ClearHistoryUseCase(
    private val sessionHistoryRepository: SessionHistoryRepository
) {
    suspend operator fun invoke() {
        sessionHistoryRepository.clearAllHistory()
    }
}
