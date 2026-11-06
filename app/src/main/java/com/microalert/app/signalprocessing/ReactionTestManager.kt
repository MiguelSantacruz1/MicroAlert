package com.microalert.app.signalprocessing

import com.microalert.app.signalprocessing.model.ReactionTestResult

/**
 * Gestor de la prueba de reacción.
 * Controla la frecuencia de ejecución y la política de caducidad del último valor (máximo 15 minutos de antigüedad).
 */
class ReactionTestManager(
    val maxValidityDurationMs: Long = 15 * 60 * 1000L // 15 minutos
) {
    private var lastResult: ReactionTestResult? = null

    fun recordResult(result: ReactionTestResult) {
        lastResult = result
    }

    fun getValidReactionTimeMs(): Float? {
        val current = lastResult ?: return null
        if (!current.isSuccessful || current.latencyMs == null) return null

        val age = System.currentTimeMillis() - current.timestampMs
        if (age > maxValidityDurationMs) {
            return null // El resultado ha caducado
        }

        return current.latencyMs.toFloat()
    }

    fun clear() {
        lastResult = null
    }
}
