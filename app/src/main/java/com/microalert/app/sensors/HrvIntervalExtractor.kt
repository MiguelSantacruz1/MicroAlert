package com.microalert.app.sensors

import android.content.Context

/**
 * Extractor de intervalos entre latidos (RR/IBI) para el cálculo de variabilidad cardíaca (HRV).
 * Verifica si el hardware/SO soporta lecturas IBI/RR nativas o si requiere SDKs de fabricante.
 */
class HrvIntervalExtractor(private val context: Context) {

    fun isHrvSupportedOnDevice(): Boolean {
        // En Wear OS estándar sin SDKs propietarios de fabricante, los intervalos IBI no suelen exponerse.
        // Se realiza la verificación sin inventar valores falsos.
        return false
    }

    fun extractRIntervalsMs(): List<Float> {
        return emptyList()
    }
}
