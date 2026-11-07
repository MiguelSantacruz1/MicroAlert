package com.microalert.app.presentation.reaction

enum class ReactionTestPhase {
    INSTRUCTIONS,
    WAITING_FOR_STIMULUS,
    STIMULUS_ACTIVE,
    COMPLETED,
    SAFETY_WARNING
}

data class ReactionTestUiState(
    val phase: ReactionTestPhase = ReactionTestPhase.INSTRUCTIONS,
    val lastLatencyMs: Long? = null,
    val isSafetyAccepted: Boolean = false,
    val message: String = "Presione cuando aparezca la señal visual o vibración."
)
