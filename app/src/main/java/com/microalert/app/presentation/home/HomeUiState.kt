package com.microalert.app.presentation.home

import com.microalert.app.domain.model.AppMode
import com.microalert.app.domain.model.RiskLevel

data class HomeUiState(
    val appMode: AppMode = AppMode.REAL,
    val isMonitoringActive: Boolean = false,
    val currentRiskLevel: RiskLevel = RiskLevel.NORMAL,
    val activeSensorsCount: Int = 3,
    val totalSensorsCount: Int = 4
)
