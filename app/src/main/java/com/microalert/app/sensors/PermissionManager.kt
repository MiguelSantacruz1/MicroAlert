package com.microalert.app.sensors

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

class PermissionManager(private val context: Context) {

    companion object {
        const val READ_HEART_RATE = "android.permission.health.READ_HEART_RATE"
    }

    fun heartRatePermission(): String {
        return if (Build.VERSION.SDK_INT >= 36) READ_HEART_RATE else Manifest.permission.BODY_SENSORS
    }

    fun hasBodySensorsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            heartRatePermission()
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasActivityRecognitionPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    fun getRequiredPermissions(): List<String> {
        val permissions = mutableListOf(
            heartRatePermission(),
            Manifest.permission.ACTIVITY_RECOGNITION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        return permissions
    }

    fun hasAllRequiredPermissions(): Boolean {
        return hasBodySensorsPermission() &&
                hasActivityRecognitionPermission() &&
                hasNotificationPermission()
    }

    fun getMissingPermissions(): List<String> {
        return getRequiredPermissions().filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }
    }
}
