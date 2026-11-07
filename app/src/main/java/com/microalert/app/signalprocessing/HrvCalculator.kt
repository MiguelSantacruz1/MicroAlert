package com.microalert.app.signalprocessing

import kotlin.math.pow
import kotlin.math.sqrt

data class HrvMetrics(
    val sdnnMs: Float?,
    val rmssdMs: Float?
)

class HrvCalculator {

    fun calculateHrv(rIntervalsMs: List<Float>): HrvMetrics {
        if (rIntervalsMs.size < 3) {
            return HrvMetrics(sdnnMs = null, rmssdMs = null)
        }

        val meanRr = rIntervalsMs.average().toFloat()
        val varianceSdnn = rIntervalsMs.sumOf { (it - meanRr).toDouble().pow(2) } / (rIntervalsMs.size - 1)
        val sdnn = sqrt(varianceSdnn).toFloat()

        var sumSuccessiveDiffSquare = 0.0
        for (i in 0 until rIntervalsMs.size - 1) {
            val diff = (rIntervalsMs[i + 1] - rIntervalsMs[i]).toDouble()
            sumSuccessiveDiffSquare += diff.pow(2)
        }
        val meanRmssd = sumSuccessiveDiffSquare / (rIntervalsMs.size - 1)
        val rmssd = sqrt(meanRmssd).toFloat()

        return HrvMetrics(sdnnMs = sdnn, rmssdMs = rmssd)
    }
}
