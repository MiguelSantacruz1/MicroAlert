package com.microalert.app.domain.model

data class AlertPolicyConfig(
    val isVibrationEnabled: Boolean = true,
    val criticalCooldownMs: Long = 20_000L,
    val highCooldownMs: Long = 45_000L,
    val precautionCooldownMs: Long = 90_000L
)
