package com.microalert.app.presentation.settings

import com.microalert.app.domain.model.AppMode

/**
 * Estado UI para la pantalla SETTINGS.
 */
data class SettingsUiState(
    val appMode: AppMode = AppMode.REAL,
    val isVibrationEnabled: Boolean = true,
    val vibrationCooldownSeconds: Int = 30,
    val isReactionTestEnabled: Boolean = true,
    val reactionTestIntervalMinutes: Int = 15,
    val windowSizeSeconds: Int = 60,
    val windowStepSeconds: Int = 30
)
