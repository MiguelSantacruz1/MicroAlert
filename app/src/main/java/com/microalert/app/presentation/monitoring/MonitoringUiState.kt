package com.microalert.app.presentation.monitoring

import com.microalert.app.domain.model.AppMode
import com.microalert.app.domain.model.RiskLevel

data class MonitoringUiState(
    val isMonitoring: Boolean = false,
    val appMode: AppMode = AppMode.REAL,
    val heartRateBpm: Float = 0f,
    val heartRateValid: Boolean = false,
    val accelerometerMagnitude: Float = 9.81f,
    val gyroscopeMagnitude: Float = 0.04f,
    val inactivityDurationSeconds: Long = 0L,
    val sessionDurationSeconds: Long = 0L,
    val currentRiskLevel: RiskLevel = RiskLevel.NORMAL,
    val hrvAvailable: Boolean = false,
    val hrvSdnnMs: Float? = null,
    val hrvRmssdMs: Float? = null,
    val windowCountProcessed: Int = 0,
    val calibrationProgress: Float? = null,
    val baselineHeartRate: Float? = null,
    val thresholdOffset: Float = 0f,
    val forecastProbability: Float? = null,
    val demoLevel: com.microalert.app.sensors.DemoLevel = com.microalert.app.sensors.DemoLevel.AUTO
)
