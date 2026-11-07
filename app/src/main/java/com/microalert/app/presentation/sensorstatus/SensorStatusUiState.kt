package com.microalert.app.presentation.sensorstatus

data class SensorStatusUiState(
    val isHeartRateAvailable: Boolean = true,
    val isAccelerometerAvailable: Boolean = true,
    val isGyroscopeAvailable: Boolean = true,
    val isHrvAvailable: Boolean = false,
    val hrvStatusNote: String = "Datos IBI/RR no expuestos en este hardware.",
    val hasBodySensorsPermission: Boolean = true,
    val hasActivityRecognitionPermission: Boolean = true
)
