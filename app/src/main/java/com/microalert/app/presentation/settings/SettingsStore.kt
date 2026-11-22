package com.microalert.app.presentation.settings

import android.content.Context
import com.microalert.app.domain.model.AppMode

class SettingsStore(context: Context) {

    private val prefs = context.getSharedPreferences("microalert_settings", Context.MODE_PRIVATE)

    fun isVibrationEnabled(): Boolean = prefs.getBoolean(KEY_VIBRATION, true)

    fun cooldownSeconds(): Int = prefs.getInt(KEY_COOLDOWN, 30)

    fun appMode(): AppMode =
        if (prefs.getString(KEY_MODE, null) == AppMode.REAL.name) AppMode.REAL else AppMode.DEMO

    fun saveAppMode(mode: AppMode) {
        prefs.edit().putString(KEY_MODE, mode.name).apply()
    }

    fun save(vibrationEnabled: Boolean, cooldownSeconds: Int) {
        prefs.edit()
            .putBoolean(KEY_VIBRATION, vibrationEnabled)
            .putInt(KEY_COOLDOWN, cooldownSeconds)
            .apply()
    }

    private companion object {
        const val KEY_MODE = "app_mode"
        const val KEY_VIBRATION = "vibration_enabled"
        const val KEY_COOLDOWN = "cooldown_seconds"
    }
}
