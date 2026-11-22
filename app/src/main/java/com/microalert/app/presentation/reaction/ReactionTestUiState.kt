package com.microalert.app.presentation.reaction

enum class ReactionTestPhase {
    INSTRUCTIONS,
    WAITING_FOR_STIMULUS,
    STIMULUS_ACTIVE,
    COMPLETED
}

data class ReactionTestUiState(
    val phase: ReactionTestPhase = ReactionTestPhase.INSTRUCTIONS,
    val lastLatencyMs: Long? = null,
    val message: String = "Toca cuando aparezca la señal."
)
