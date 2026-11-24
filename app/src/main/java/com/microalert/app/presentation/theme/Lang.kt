package com.microalert.app.presentation.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.microalert.app.domain.model.RiskLevel
import com.microalert.app.sensors.DemoLevel

/** Idioma de la interfaz. Estado observable: al cambiarlo se recomponen las pantallas. */
object Lang {
    var isEnglish by mutableStateOf(false)
}

fun tr(es: String, en: String): String = if (Lang.isEnglish) en else es

val RiskLevel.localLabel: String
    get() = when (this) {
        RiskLevel.NORMAL -> "NORMAL"
        RiskLevel.PRECAUTION -> tr("PRECAUCIÓN", "CAUTION")
        RiskLevel.HIGH -> tr("ALTO", "HIGH")
        RiskLevel.CRITICAL -> tr("CRÍTICO", "CRITICAL")
    }

val RiskLevel.localDescription: String
    get() = when (this) {
        RiskLevel.NORMAL -> tr("Sin patrones significativos de somnolencia.", "No significant drowsiness patterns.")
        RiskLevel.PRECAUTION -> tr("Patrones iniciales detectados. Manténgase atento.", "Early patterns detected. Stay alert.")
        RiskLevel.HIGH -> tr("Indicadores elevados de fatiga. Se recomienda descanso.", "High fatigue indicators. Rest is advised.")
        RiskLevel.CRITICAL -> tr("¡ALERTA MÁXIMA! Patrón crítico de somnolencia detectado.", "MAX ALERT! Critical drowsiness pattern detected.")
    }

val RiskLevel.localAlertText: String
    get() = when (this) {
        RiskLevel.CRITICAL -> tr(
            "¡PATRÓN CRÍTICO DETECTADO! Detenga la actividad y descanse de inmediato.",
            "CRITICAL PATTERN DETECTED! Stop and rest immediately."
        )
        RiskLevel.HIGH -> tr(
            "Nivel de riesgo elevado. Indicadores coincidentes de fatiga.",
            "Elevated risk level. Matching fatigue indicators."
        )
        RiskLevel.PRECAUTION -> tr(
            "Patrones iniciales detectados. Manténgase alerta.",
            "Early patterns detected. Stay alert."
        )
        RiskLevel.NORMAL -> tr(
            "¡ALERTA DE SOMNOLENCIA! Se detectó un patrón de riesgo elevado.",
            "DROWSINESS ALERT! An elevated risk pattern was detected."
        )
    }

val DemoLevel.localLabel: String
    get() = when (this) {
        DemoLevel.AUTO -> "AUTO"
        DemoLevel.NORMAL -> "NORMAL"
        DemoLevel.PRECAUTION -> tr("PRECAUCIÓN", "CAUTION")
        DemoLevel.HIGH -> tr("ALTO", "HIGH")
        DemoLevel.CRITICAL -> tr("CRÍTICO", "CRITICAL")
    }
