package com.microalert.app.presentation.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microalert.app.domain.model.AlertEvent
import com.microalert.app.domain.usecase.ManageAlertsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlertViewModel(
    private val manageAlertsUseCase: ManageAlertsUseCase? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(AlertUiState())
    val uiState: StateFlow<AlertUiState> = _uiState.asStateFlow()

    init {
        manageAlertsUseCase?.let { useCase ->
            viewModelScope.launch {
                useCase.activeAlert.collect { alert ->
                    if (alert != null) {
                        _uiState.update {
                            it.copy(
                                alertId = alert.id,
                                riskLevel = alert.riskLevel,
                                alertMessage = alert.message,
                                probability = alert.probability,
                                isAcknowledged = alert.isAcknowledged,
                                timestampMs = alert.timestampMs
                            )
                        }
                    }
                }
            }
        }
    }

    fun onAlertTriggered(alert: AlertEvent) {
        _uiState.update {
            it.copy(
                alertId = alert.id,
                riskLevel = alert.riskLevel,
                alertMessage = alert.message,
                probability = alert.probability,
                isAcknowledged = false,
                timestampMs = alert.timestampMs
            )
        }
    }

    fun acknowledgeAlert(): AlertEvent? {
        val acknowledged = manageAlertsUseCase?.acknowledgeAlert()
        _uiState.update { it.copy(isAcknowledged = true) }
        return acknowledged
    }
}
