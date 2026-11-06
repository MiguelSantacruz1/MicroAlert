package com.microalert.app.signalprocessing

import com.microalert.app.sensors.model.SensorSample
import com.microalert.app.signalprocessing.model.ProcessedWindowData

/**
 * Agregador de características fisiológicas y cinemáticas dentro de una ventana temporal delimitada.
 */
class FeatureAggregator {

    private val hrvCalculator = HrvCalculator()
    private val inactivityDetector = InactivityDetector()

    fun aggregateWindow(samples: List<SensorSample>): ProcessedWindowData {
        if (samples.isEmpty()) {
            return ProcessedWindowData(
                windowStartTimestampMs = System.currentTimeMillis(),
                windowEndTimestampMs = System.currentTimeMillis(),
                avgHeartRateBpm = null,
                avgAccelerometerMagnitude = 0f,
                avgGyroscopeMagnitude = 0f,
                inactivitySeconds = 0L,
                hrvSdnnMs = null,
                hrvRmssdMs = null,
                sampleCount = 0,
                isHrvAvailable = false
            )
        }

        val startTime = samples.first().timestampMs
        val endTime = samples.last().timestampMs

        val validHeartRates = samples.mapNotNull { it.heartRateBpm }
        val avgHr = if (validHeartRates.isNotEmpty()) validHeartRates.average().toFloat() else null

        val avgAccelMag = samples.map { it.accelMagnitude }.average().toFloat()
        val avgGyroMag = samples.map { it.gyroMagnitude }.average().toFloat()

        val allRIntervals = samples.flatMap { it.rIntervalsMs }
        val hrvMetrics = hrvCalculator.calculateHrv(allRIntervals)

        val inactivitySec = inactivityDetector.calculateInactivityDurationSeconds(samples)

        return ProcessedWindowData(
            windowStartTimestampMs = startTime,
            windowEndTimestampMs = endTime,
            avgHeartRateBpm = avgHr,
            avgAccelerometerMagnitude = avgAccelMag,
            avgGyroscopeMagnitude = avgGyroMag,
            inactivitySeconds = inactivitySec,
            hrvSdnnMs = hrvMetrics.sdnnMs,
            hrvRmssdMs = hrvMetrics.rmssdMs,
            sampleCount = samples.size,
            isHrvAvailable = hrvMetrics.sdnnMs != null
        )
    }
}
