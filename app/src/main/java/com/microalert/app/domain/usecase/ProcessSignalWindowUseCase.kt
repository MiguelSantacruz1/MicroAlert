package com.microalert.app.domain.usecase

import com.microalert.app.domain.model.SignalWindow
import com.microalert.app.signalprocessing.SignalProcessingPipeline
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Caso de uso para exponer las ventanas procesadas convertidas a modelos de dominio.
 */
class ProcessSignalWindowUseCase(
    private val pipeline: SignalProcessingPipeline
) {
    operator fun invoke(): Flow<SignalWindow> {
        return pipeline.windowStream.map { window ->
            SignalWindow(
                timestampMs = window.windowEndTimestampMs,
                heartRateBpm = window.avgHeartRateBpm,
                accelMagnitude = window.avgAccelerometerMagnitude,
                gyroMagnitude = window.avgGyroscopeMagnitude,
                inactivityDurationSeconds = window.inactivitySeconds,
                hrvSdnnMs = window.hrvSdnnMs,
                hrvRmssdMs = window.hrvRmssdMs
            )
        }
    }
}
