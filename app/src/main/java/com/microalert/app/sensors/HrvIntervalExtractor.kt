package com.microalert.app.sensors

import android.content.Context

class HrvIntervalExtractor(private val context: Context) {

    fun isHrvSupportedOnDevice(): Boolean {

        return false
    }

    fun extractRIntervalsMs(): List<Float> {
        return emptyList()
    }
}
