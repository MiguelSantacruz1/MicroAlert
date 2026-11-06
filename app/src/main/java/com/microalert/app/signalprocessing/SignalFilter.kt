package com.microalert.app.signalprocessing

/**
 * Filtro de suavizado de media móvil (Moving Average Filter) para señales fisiológicas.
 */
class SignalFilter(val windowSize: Int = 5) {

    private val values = ArrayDeque<Float>()

    fun filter(newValue: Float): Float {
        values.addLast(newValue)
        if (values.size > windowSize) {
            values.removeFirst()
        }
        return values.average().toFloat()
    }

    fun clear() {
        values.clear()
    }
}
