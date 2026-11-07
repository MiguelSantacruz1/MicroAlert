package com.microalert.app.signalprocessing

import com.microalert.app.sensors.model.SensorSample
import java.util.ArrayDeque

class BufferWindowManager(
    val windowDurationMs: Long = 60_000L,
    val windowStepMs: Long = 30_000L,
    val emitOnFirstSample: Boolean = false
) {
    private val samplesBuffer = ArrayDeque<SensorSample>()
    private var lastEmittedTimestampMs: Long = 0L

    fun addSample(sample: SensorSample): List<SensorSample>? {
        samplesBuffer.addLast(sample)

        val cutoffTimestamp = sample.timestampMs - windowDurationMs
        while (samplesBuffer.isNotEmpty() && samplesBuffer.first.timestampMs < cutoffTimestamp) {
            samplesBuffer.removeFirst()
        }

        if (lastEmittedTimestampMs == 0L) {
            lastEmittedTimestampMs = sample.timestampMs
            return if (emitOnFirstSample) samplesBuffer.toList() else null
        }

        if ((sample.timestampMs - lastEmittedTimestampMs) >= windowStepMs) {
            lastEmittedTimestampMs = sample.timestampMs
            return samplesBuffer.toList()
        }

        return null
    }

    fun clear() {
        samplesBuffer.clear()
        lastEmittedTimestampMs = 0L
    }

    fun getBufferSnapshot(): List<SensorSample> = samplesBuffer.toList()
}
