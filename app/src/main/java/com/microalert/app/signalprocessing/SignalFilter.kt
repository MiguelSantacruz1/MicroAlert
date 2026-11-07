package com.microalert.app.signalprocessing

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
