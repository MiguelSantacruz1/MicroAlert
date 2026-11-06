package com.microalert.app.signalprocessing

import com.microalert.app.sensors.model.SensorSample
import kotlin.math.abs

/**
 * Detector de inactividad física basado en la acelerometría y variaciones de movimiento.
 */
class InactivityDetector(
    val movementThreshold: Float = 0.35f
) {
    fun calculateInactivityDurationSeconds(samples: List<SensorSample>): Long {
        if (samples.isEmpty()) return 0L

        var inactiveCount = 0L
        val baseGravity = 9.81f

        for (sample in samples) {
            val delta = abs(sample.accelMagnitude - baseGravity)
            if (delta < movementThreshold) {
                inactiveCount++
            }
        }

        // Estimación del tiempo considerando una frecuencia de muestreo de ~2 Hz (0.5s por muestra)
        return (inactiveCount * 0.5f).toLong()
    }
}
