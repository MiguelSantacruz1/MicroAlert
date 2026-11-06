package com.microalert.app.domain.model

/**
 * Configuración de la política de emisión y enfriamiento (cooldown) de alertas.
 * Evita la saturación del usuario y el agotamiento de batería por vibraciones repetitivas.
 */
data class AlertPolicyConfig(
    val isVibrationEnabled: Boolean = true,
    val criticalCooldownMs: Long = 20_000L, // 20 segundos entre alertas críticas
    val highCooldownMs: Long = 45_000L,     // 45 segundos entre alertas de riesgo alto
    val precautionCooldownMs: Long = 90_000L // 90 segundos entre avisos de precaución
)
