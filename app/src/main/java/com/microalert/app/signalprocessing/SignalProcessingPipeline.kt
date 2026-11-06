package com.microalert.app.signalprocessing

import com.microalert.app.sensors.model.SensorSample
import com.microalert.app.signalprocessing.model.ProcessedWindowData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Pipeline unificado de procesamiento de señales en tiempo real.
 * Recibe muestras de sensores y emite objetos ProcessedWindowData cuando se completa cada ventana de 30s.
 */
class SignalProcessingPipeline(
    windowDurationMs: Long = 60_000L,
    windowStepMs: Long = 30_000L
) {
    private val bufferManager = BufferWindowManager(windowDurationMs, windowStepMs)
    private val aggregator = FeatureAggregator()

    private val _windowStream = MutableSharedFlow<ProcessedWindowData>(extraBufferCapacity = 10)
    val windowStream: Flow<ProcessedWindowData> = _windowStream.asSharedFlow()

    suspend fun processSample(sample: SensorSample) {
        val fullWindowSamples = bufferManager.addSample(sample)
        if (fullWindowSamples != null) {
            val processedWindow = aggregator.aggregateWindow(fullWindowSamples)
            _windowStream.emit(processedWindow)
        }
    }

    fun reset() {
        bufferManager.clear()
    }
}
