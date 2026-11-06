package com.microalert.app.services

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * Servicio en primer plano para monitoreo continuo de sensores fisiológicos en Wear OS.
 */
class MonitoringForegroundService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }
}
