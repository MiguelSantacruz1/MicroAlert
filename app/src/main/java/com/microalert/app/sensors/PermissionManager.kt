package com.microalert.app.sensors

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Gestor de permisos para sensores corporales y reconocimiento de actividad física.
 */
class PermissionManager(private val context: Context) {

    fun hasBodySensorsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.BODY_SENSORS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasActivityRecognitionPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getRequiredPermissions(): List<String> {
        return listOf(
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
    }
}
