package com.microalert.app.signalprocessing

import com.microalert.app.signalprocessing.model.ReactionTestResult

class ReactionTestManager(
    val maxValidityDurationMs: Long = 15 * 60 * 1000L
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
            return null
        }

        return current.latencyMs.toFloat()
    }

    fun clear() {
        lastResult = null
    }
}
