package com.microalert.app.domain.model

import androidx.compose.ui.graphics.Color

/**
 * Representa los 4 niveles de riesgo de somnolencia identificados por MICROALERT.
 */
enum class RiskLevel(
    val label: String,
    val description: String,
    val color: Color
) {
    NORMAL(
        label = "NORMAL",
        description = "Sin patrones significativos de somnolencia.",
        color = Color(0xFF4CAF50) // Verde
    ),
    PRECAUTION(
        label = "PRECAUCIÓN",
        description = "Patrones iniciales detectados. Manténgase atento.",
        color = Color(0xFFFFC107) // Amarillo / Ámbar
    ),
    HIGH(
        label = "ALTO",
        description = "Indicadores elevados de fatiga. Se recomienda descanso.",
        color = Color(0xFFFF9800) // Naranja
    ),
    CRITICAL(
        label = "CRÍTICO",
        description = "¡ALERTA MÁXIMA! Patrón crítico de somnolencia detectado.",
        color = Color(0xFFF44336) // Rojo
    )
}
