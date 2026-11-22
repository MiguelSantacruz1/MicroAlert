package com.microalert.app.data.repository

import android.content.Context
import com.microalert.app.domain.model.AppMode
import com.microalert.app.sensors.DemoSensorSimulator
import com.microalert.app.sensors.SensorDataCollector
import com.microalert.app.sensors.SensorDataSource
import com.microalert.app.sensors.model.SensorSample
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class SensorRepositoryImpl(context: Context) : SensorRepository {

    private val realCollector = SensorDataCollector(context)
    private val demoSimulator = DemoSensorSimulator()

    private val activeMode = MutableStateFlow(AppMode.REAL)

    private val activeSource: SensorDataSource
        get() = if (activeMode.value == AppMode.REAL) realCollector else demoSimulator

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getSensorDataStream(): Flow<SensorSample> {
        return activeMode.flatMapLatest { mode ->
            if (mode == AppMode.REAL) realCollector.sensorDataStream else demoSimulator.sensorDataStream
        }
    }

    override fun currentMode(): AppMode = activeMode.value

    override fun selectMode(appMode: AppMode) {
        activeMode.value = appMode
    }

    override fun startMonitoring(appMode: AppMode) {
        activeMode.value = appMode
        activeSource.startSampling()
    }

    override fun stopMonitoring() {
        realCollector.stopSampling()
        demoSimulator.stopSampling()
    }

    override fun isHeartRateAvailable(): Boolean = activeSource.isHeartRateAvailable()
    override fun isAccelerometerAvailable(): Boolean = activeSource.isAccelerometerAvailable()
    override fun isGyroscopeAvailable(): Boolean = activeSource.isGyroscopeAvailable()
    override fun isHrvAvailable(): Boolean = activeSource.isHrvAvailable()
}
