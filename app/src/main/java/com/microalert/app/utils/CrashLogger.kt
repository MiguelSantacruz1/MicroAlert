package com.microalert.app.utils

import android.util.Log

/**
 * Registrador local de diagnósticos y fallos no fatales sin dependencias de servicios externos.
 */
object CrashLogger {
    private const val TAG = "MICROALERT_DIAGNOSTICS"

    fun logInfo(message: String) {
        Log.i(TAG, message)
    }

    fun logWarning(message: String, throwable: Throwable? = null) {
        Log.w(TAG, message, throwable)
    }

    fun logError(message: String, throwable: Throwable? = null) {
        Log.e(TAG, message, throwable)
    }
}
