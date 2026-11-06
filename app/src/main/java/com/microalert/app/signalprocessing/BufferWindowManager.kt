package com.microalert.app.signalprocessing

import com.microalert.app.sensors.model.SensorSample
import java.util.ArrayDeque

/**
 * Gestor de ventana temporal deslizante.
 * Almacena muestras durante 60 segundos y emite ventanas cada 30 segundos (paso/step).
 */
class BufferWindowManager(
    val windowDurationMs: Long = 60_000L,
    val windowStepMs: Long = 30_000L
) {
    private val samplesBuffer = ArrayDeque<SensorSample>()
    private var lastEmittedTimestampMs: Long = 0L

    fun addSample(sample: SensorSample): List<SensorSample>? {
        samplesBuffer.addLast(sample)

        // Eliminar muestras más antiguas que la duración de la ventana
        val cutoffTimestamp = sample.timestampMs - windowDurationMs
        while (samplesBuffer.isNotEmpty() && samplesBuffer.first.timestampMs < cutoffTimestamp) {
            samplesBuffer.removeFirst()
        }

        // Verificar si ha transcurrido el tiempo de paso desde la última emisión
        if (lastEmittedTimestampMs == 0L || (sample.timestampMs - lastEmittedTimestampMs) >= windowStepMs) {
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
