package com.microalert.app.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings

/**
 * Asistente para verificar el estado de optimización de batería en Wear OS y solicitar
 * exención de Doze/Standby para monitoreo crítico en tiempo real.
 */
class BatteryOptimizationHelper(private val context: Context) {

    fun isIgnoringBatteryOptimizations(): Boolean {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as? PowerManager
        return powerManager?.isIgnoringBatteryOptimizations(context.packageName) ?: true
    }

    fun createRequestIgnoreBatteryOptimizationsIntent(): Intent? {
        return if (!isIgnoringBatteryOptimizations()) {
            Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:${context.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        } else {
            null
        }
    }
}
