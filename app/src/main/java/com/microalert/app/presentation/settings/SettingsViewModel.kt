package com.microalert.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microalert.app.domain.alerts.AlertManager
import com.microalert.app.domain.usecase.ClearHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val clearHistoryUseCase: ClearHistoryUseCase? = null,
    private val alertManager: AlertManager? = null,
    private val store: SettingsStore? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            isVibrationEnabled = store?.isVibrationEnabled() ?: true,
            vibrationCooldownSeconds = store?.cooldownSeconds() ?: 30
        )
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        applyToAlerts(_uiState.value)
    }

    fun toggleVibration() {
        change { it.copy(isVibrationEnabled = !it.isVibrationEnabled) }
    }

    fun cycleCooldown() {
        change {
            val next = when (it.vibrationCooldownSeconds) {
                15 -> 30
                30 -> 45
                45 -> 60
                else -> 15
            }
            it.copy(vibrationCooldownSeconds = next)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            clearHistoryUseCase?.invoke()
        }
    }

    private fun change(transform: (SettingsUiState) -> SettingsUiState) {
        _uiState.update(transform)
        val state = _uiState.value
        store?.save(state.isVibrationEnabled, state.vibrationCooldownSeconds)
        applyToAlerts(state)
    }

    private fun applyToAlerts(state: SettingsUiState) {
        val manager = alertManager ?: return
        val base = state.vibrationCooldownSeconds * 1000L
        manager.config = manager.config.copy(
            isVibrationEnabled = state.isVibrationEnabled,
            criticalCooldownMs = base,
            highCooldownMs = base * 2,
            precautionCooldownMs = base * 3
        )
    }
}
