package com.microalert.app.utils

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.microalert.app.domain.model.RiskLevel

/**
 * Gestor del hardware de vibración háptica para Wear OS.
 * Implementa patrones diferenciados para cada nivel de riesgo de somnolencia
 * con compatibilidad hacia atrás (API 30+) y soporte de VibratorManager (API 31+).
 */
class HapticFeedbackManager(private val context: Context) {

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun hasVibrator(): Boolean = vibrator?.hasVibrator() == true

    /**
     * Emite el patrón de vibración háptica correspondiente al nivel de riesgo.
     */
    fun triggerRiskAlertHaptic(riskLevel: RiskLevel) {
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return

        when (riskLevel) {
            RiskLevel.CRITICAL -> {
                // Patrón crítico: 3 pulsos intensos consecutivos con repetición agresiva (SOS háptico)
                // [espera, vibra, espera, vibra, espera, vibra...]
                val timings = longArrayOf(0, 300, 150, 300, 150, 450)
                val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255)
                vibrateWithPattern(vib, timings, amplitudes)
            }
            RiskLevel.HIGH -> {
                // Patrón alto: 2 pulsos firmes de advertencia
                val timings = longArrayOf(0, 250, 150, 250)
                val amplitudes = intArrayOf(0, 200, 0, 200)
                vibrateWithPattern(vib, timings, amplitudes)
            }
            RiskLevel.PRECAUTION -> {
                // Patrón de precaución: 1 pulso suave de atención
                val timings = longArrayOf(0, 150)
                val amplitudes = intArrayOf(0, 120)
                vibrateWithPattern(vib, timings, amplitudes)
            }
            RiskLevel.NORMAL -> {
                // Sin vibración en estado normal
            }
        }
    }

    /**
     * Emite un pulso suave para confirmar la interacción del usuario al reconocer una alerta.
     */
    fun triggerAcknowledgementHaptic() {
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return
        val effect = VibrationEffect.createOneShot(80, VibrationEffect.DEFAULT_AMPLITUDE)
        vib.vibrate(effect)
    }

    private fun vibrateWithPattern(vib: Vibrator, timings: LongArray, amplitudes: IntArray) {
        if (vib.hasAmplitudeControl()) {
            val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vib.vibrate(effect)
        } else {
            val effect = VibrationEffect.createWaveform(timings, -1)
            vib.vibrate(effect)
        }
    }

    fun cancelVibration() {
        vibrator?.cancel()
    }
}
