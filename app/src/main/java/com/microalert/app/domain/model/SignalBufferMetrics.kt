package com.microalert.app.domain.model

data class SignalBufferMetrics(
    val totalSamplesReceived: Long = 0L,
    val totalWindowsEmitted: Long = 0L,
    val droppedSamplesOutOfOrder: Long = 0L,
    val currentBufferCapacity: Int = 0
) {
    val dropRatePercent: Float
        get() = if (totalSamplesReceived > 0) {
            (droppedSamplesOutOfOrder.toFloat() / totalSamplesReceived.toFloat()) * 100f
        } else 0f
}
