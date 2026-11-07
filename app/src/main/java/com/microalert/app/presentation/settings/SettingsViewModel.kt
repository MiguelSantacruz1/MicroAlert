package com.microalert.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microalert.app.domain.model.AppMode
import com.microalert.app.domain.usecase.ClearHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val clearHistoryUseCase: ClearHistoryUseCase? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun toggleAppMode() {
        _uiState.update { current ->
            val nextMode = if (current.appMode == AppMode.REAL) AppMode.DEMO else AppMode.REAL
            current.copy(appMode = nextMode)
        }
    }

    fun toggleVibration() {
        _uiState.update { current ->
            current.copy(isVibrationEnabled = !current.isVibrationEnabled)
        }
    }

    fun toggleReactionTest() {
        _uiState.update { current ->
            current.copy(isReactionTestEnabled = !current.isReactionTestEnabled)
        }
    }

    fun cycleCooldown() {
        _uiState.update { current ->
            val nextCooldown = when (current.vibrationCooldownSeconds) {
                15 -> 30
                30 -> 45
                45 -> 60
                else -> 15
            }
            current.copy(vibrationCooldownSeconds = nextCooldown)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            clearHistoryUseCase?.invoke()
        }
    }
}
