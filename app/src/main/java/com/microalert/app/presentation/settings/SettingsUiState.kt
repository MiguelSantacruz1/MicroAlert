package com.microalert.app.presentation.settings

data class SettingsUiState(
    val isVibrationEnabled: Boolean = true,
    val vibrationCooldownSeconds: Int = 30
)
