package com.microalert.app.presentation.monitoring

import com.microalert.app.domain.model.AppMode
import com.microalert.app.domain.model.RiskLevel

/**
 * Estado UI para la pantalla de Monitoreo en vivo con métricas procesadas de ventana.
 */
data class MonitoringUiState(
    val isMonitoring: Boolean = true,
    val appMode: AppMode = AppMode.REAL,
    val heartRateBpm: Float = 72.0f,
    val accelerometerMagnitude: Float = 9.81f,
    val gyroscopeMagnitude: Float = 0.04f,
    val inactivityDurationSeconds: Long = 15L,
    val sessionDurationSeconds: Long = 120L,
    val currentRiskLevel: RiskLevel = RiskLevel.NORMAL,
    val hrvAvailable: Boolean = false,
    val hrvSdnnMs: Float? = null,
    val hrvRmssdMs: Float? = null,
    val windowCountProcessed: Int = 0
)
