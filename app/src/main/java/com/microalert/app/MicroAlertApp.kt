package com.microalert.app

import android.app.Application

/**
 * Clase principal Application para MICROALERT.
 * Maneja la inicialización global del contenedor de dependencias manual (AppContainer).
 */
class MicroAlertApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // Inicialización global del contenedor de dependencias si es necesario
    }
}
