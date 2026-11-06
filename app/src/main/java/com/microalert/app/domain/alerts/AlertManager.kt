package com.microalert.app.domain.alerts

import com.microalert.app.domain.model.AlertEvent
import com.microalert.app.domain.model.AlertPolicyConfig
import com.microalert.app.domain.model.RiskAssessmentResult
import com.microalert.app.domain.model.RiskLevel
import com.microalert.app.utils.HapticFeedbackManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Gestor del sistema de alertas visuales y hápticas.
 * Controla el ciclo de vida de la alerta activa, el tiempo de enfriamiento (cooldown)
 * para evitar vibraciones excesivas y el registro del reconocimiento por el usuario.
 */
class AlertManager(
    private val hapticFeedbackManager: HapticFeedbackManager? = null,
    val config: AlertPolicyConfig = AlertPolicyConfig()
) {
    private val _activeAlert = MutableStateFlow<AlertEvent?>(null)
    val activeAlert: StateFlow<AlertEvent?> = _activeAlert.asStateFlow()

    private val _alertEventStream = MutableSharedFlow<AlertEvent>(extraBufferCapacity = 20)
    val alertEventStream: SharedFlow<AlertEvent> = _alertEventStream.asSharedFlow()

    private var lastAlertTimestampMs: Long = 0L
    private var lastAlertLevel: RiskLevel? = null

    /**
     * Procesa el resultado de evaluación de riesgo y determina si corresponde disparar una alerta.
     * Retorna el evento creado o null si se encuentra en periodo de enfriamiento (cooldown).
     */
    fun processRiskAssessment(
        assessment: RiskAssessmentResult,
        currentTimeMs: Long = System.currentTimeMillis()
    ): AlertEvent? {
        val level = assessment.effectiveRiskLevel
        if (level == RiskLevel.NORMAL) {
            return null
        }

        // Verificar periodo de enfriamiento (cooldown) según el nivel
        val cooldownMs = when (level) {
            RiskLevel.CRITICAL -> config.criticalCooldownMs
            RiskLevel.HIGH -> config.highCooldownMs
            RiskLevel.PRECAUTION -> config.precautionCooldownMs
            RiskLevel.NORMAL -> 0L
        }

        val elapsedSinceLast = currentTimeMs - lastAlertTimestampMs
        val isCooldownActive = lastAlertTimestampMs > 0L && elapsedSinceLast < cooldownMs && lastAlertLevel == level

        if (isCooldownActive) {
            return null // Bloqueado por enfriamiento para no fatigar al usuario
        }

        val message = when (level) {
            RiskLevel.CRITICAL -> "¡PATRÓN CRÍTICO DETECTADO! Detenga la actividad y descanse de inmediato."
            RiskLevel.HIGH -> "Nivel de riesgo elevado. Indicadores coincidentes de fatiga."
            RiskLevel.PRECAUTION -> "Patrones iniciales detectados. Manténgase alerta."
            RiskLevel.NORMAL -> ""
        }

        val alertEvent = AlertEvent(
            timestampMs = currentTimeMs,
            riskLevel = level,
            probability = assessment.drowsinessProbability,
            message = message
        )

        lastAlertTimestampMs = currentTimeMs
        lastAlertLevel = level
        _activeAlert.value = alertEvent
        _alertEventStream.tryEmit(alertEvent)

        // Activar vibración háptica si está habilitada
        if (config.isVibrationEnabled) {
            hapticFeedbackManager?.triggerRiskAlertHaptic(level)
        }

        return alertEvent
    }

    /**
     * Reconoce la alerta activa actual, cancela vibraciones continuas y emite háptica de confirmación.
     */
    fun acknowledgeActiveAlert(acknowledgedAtMs: Long = System.currentTimeMillis()): AlertEvent? {
        val current = _activeAlert.value ?: return null
        val acknowledgedAlert = current.acknowledge(acknowledgedAtMs)

        _activeAlert.value = null
        hapticFeedbackManager?.cancelVibration()
        hapticFeedbackManager?.triggerAcknowledgementHaptic()

        return acknowledgedAlert
    }

    fun dismissAlert() {
        _activeAlert.value = null
        hapticFeedbackManager?.cancelVibration()
    }

    fun reset() {
        _activeAlert.value = null
        lastAlertTimestampMs = 0L
        lastAlertLevel = null
        hapticFeedbackManager?.cancelVibration()
    }
}
