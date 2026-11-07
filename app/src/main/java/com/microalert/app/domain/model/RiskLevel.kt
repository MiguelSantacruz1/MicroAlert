package com.microalert.app.domain.model

import androidx.compose.ui.graphics.Color

enum class RiskLevel(
    val label: String,
    val description: String,
    val color: Color
) {
    NORMAL(
        label = "NORMAL",
        description = "Sin patrones significativos de somnolencia.",
        color = Color(0xFF4CAF50)
    ),
    PRECAUTION(
        label = "PRECAUCIÓN",
        description = "Patrones iniciales detectados. Manténgase atento.",
        color = Color(0xFFFFC107)
    ),
    HIGH(
        label = "ALTO",
        description = "Indicadores elevados de fatiga. Se recomienda descanso.",
        color = Color(0xFFFF9800)
    ),
    CRITICAL(
        label = "CRÍTICO",
        description = "¡ALERTA MÁXIMA! Patrón crítico de somnolencia detectado.",
        color = Color(0xFFF44336)
    )
}
