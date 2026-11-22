package com.microalert.app.presentation.home

import com.microalert.app.domain.model.AppMode

data class HomeUiState(
    val appMode: AppMode = AppMode.DEMO,
    val isMonitoringActive: Boolean = false,
    val activeSensorsCount: Int = 0,
    val totalSensorsCount: Int = 4
)
