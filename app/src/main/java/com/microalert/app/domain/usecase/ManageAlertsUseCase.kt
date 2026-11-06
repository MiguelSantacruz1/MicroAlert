package com.microalert.app.domain.usecase

import com.microalert.app.domain.alerts.AlertManager
import com.microalert.app.domain.model.AlertEvent
import com.microalert.app.domain.model.RiskAssessmentResult
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Caso de uso para gestionar la activación, reconocimiento y observación de alertas del sistema.
 */
class ManageAlertsUseCase(
    private val alertManager: AlertManager
) {
    val activeAlert: StateFlow<AlertEvent?> = alertManager.activeAlert
    val alertEventStream: SharedFlow<AlertEvent> = alertManager.alertEventStream

    fun onRiskAssessed(assessment: RiskAssessmentResult, currentTimeMs: Long = System.currentTimeMillis()): AlertEvent? {
        return alertManager.processRiskAssessment(assessment, currentTimeMs)
    }

    fun acknowledgeAlert(acknowledgedAtMs: Long = System.currentTimeMillis()): AlertEvent? {
        return alertManager.acknowledgeActiveAlert(acknowledgedAtMs)
    }

    fun dismissAlert() {
        alertManager.dismissAlert()
    }

    fun reset() {
        alertManager.reset()
    }
}
