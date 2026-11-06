package com.microalert.app.presentation.home

import androidx.lifecycle.ViewModel
import com.microalert.app.domain.model.AppMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * ViewModel para gestionar el estado y acciones de la pantalla Home.
 */
class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun toggleAppMode() {
        _uiState.update { currentState ->
            val nextMode = if (currentState.appMode == AppMode.REAL) AppMode.DEMO else AppMode.REAL
            currentState.copy(appMode = nextMode)
        }
    }

    fun toggleMonitoring() {
        _uiState.update { currentState ->
            currentState.copy(isMonitoringActive = !currentState.isMonitoringActive)
        }
    }
}
