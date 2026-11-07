package com.microalert.app.data.repository

import android.content.Context
import com.microalert.app.domain.model.AppMode
import com.microalert.app.sensors.DemoSensorSimulator
import com.microalert.app.sensors.SensorDataCollector
import com.microalert.app.sensors.SensorDataSource
import com.microalert.app.sensors.model.SensorSample
import com.microalert.app.signalprocessing.SignalProcessingPipeline
import com.microalert.app.signalprocessing.model.ProcessedWindowData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class SensorRepositoryImpl(context: Context) : SensorRepository {

    private val realCollector = SensorDataCollector(context)
    private val demoSimulator = DemoSensorSimulator()
    val signalPipeline = SignalProcessingPipeline()

    private val activeMode = MutableStateFlow(AppMode.REAL)
    private var activeSource: SensorDataSource = realCollector

    private var processingJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun getSensorDataStream(): Flow<SensorSample> {
        return activeMode.flatMapLatest { mode ->
            activeSource = if (mode == AppMode.REAL) realCollector else demoSimulator
            activeSource.sensorDataStream
        }
    }

    fun getProcessedWindowStream(): Flow<ProcessedWindowData> {
        return signalPipeline.windowStream
    }

    override fun startMonitoring(appMode: AppMode) {
        activeMode.value = appMode
        activeSource = if (appMode == AppMode.REAL) realCollector else demoSimulator
        activeSource.startSampling()

        if (processingJob == null || processingJob?.isCancelled == true) {
            processingJob = scope.launch {
                getSensorDataStream().collect { sample ->
                    signalPipeline.processSample(sample)
                }
            }
        }
    }

    override fun stopMonitoring() {
        processingJob?.cancel()
        processingJob = null
        realCollector.stopSampling()
        demoSimulator.stopSampling()
        signalPipeline.reset()
    }

    override fun isHeartRateAvailable(): Boolean = activeSource.isHeartRateAvailable()
    override fun isAccelerometerAvailable(): Boolean = activeSource.isAccelerometerAvailable()
    override fun isGyroscopeAvailable(): Boolean = activeSource.isGyroscopeAvailable()
    override fun isHrvAvailable(): Boolean = activeSource.isHrvAvailable()
}
