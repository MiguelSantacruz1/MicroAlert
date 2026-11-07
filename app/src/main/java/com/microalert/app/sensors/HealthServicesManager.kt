package com.microalert.app.sensors

import android.content.Context
import androidx.health.services.client.HealthServices
import androidx.health.services.client.MeasureCallback
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.DeltaDataType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HealthServicesManager(context: Context) {

    private val healthServicesClient = HealthServices.getClient(context)
    private val measureClient = healthServicesClient.measureClient

    private val _bpmFromHealthServices = MutableStateFlow<Float?>(null)
    val bpmFromHealthServices: StateFlow<Float?> = _bpmFromHealthServices.asStateFlow()

    private var isMeasuring = false

    private val callback = object : MeasureCallback {
        override fun onAvailabilityChanged(dataType: DeltaDataType<*, *>, availability: androidx.health.services.client.data.Availability) {

        }

        override fun onDataReceived(data: DataPointContainer) {
            val heartRateDataPoints = data.getData(DataType.HEART_RATE_BPM)
            if (heartRateDataPoints.isNotEmpty()) {
                val latestBpm = heartRateDataPoints.last().value
                _bpmFromHealthServices.value = latestBpm.toFloat()
            }
        }
    }

    fun isHealthServicesAvailable(): Boolean = true

    fun startMeasurement() {
        if (!isMeasuring) {
            measureClient.registerMeasureCallback(DataType.HEART_RATE_BPM, callback)
            isMeasuring = true
        }
    }

    fun stopMeasurement() {
        if (isMeasuring) {
            measureClient.unregisterMeasureCallbackAsync(DataType.HEART_RATE_BPM, callback)
            isMeasuring = false
        }
    }
}
