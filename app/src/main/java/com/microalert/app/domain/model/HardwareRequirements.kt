package com.microalert.app.domain.model

object HardwareRequirements {
    const val MIN_RAM_MB = 512
    const val RECOMMENDED_RAM_MB = 1024

    val REQUIRED_SENSORS = listOf(
        "android.sensor.accelerometer",
        "android.sensor.gyroscope",
        "android.sensor.heart_rate"
    )

    val OPTIONAL_SENSORS = listOf(
        "android.sensor.heart_rate_raw"
    )
}
