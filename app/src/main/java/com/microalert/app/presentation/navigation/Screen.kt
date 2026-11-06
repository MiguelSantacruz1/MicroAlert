package com.microalert.app.presentation.navigation

/**
 * Rutas declarativas y nombres de pantalla para la navegación en Wear OS.
 */
sealed class AppDestination(val route: String, val title: String) {
    object Home : AppDestination(Screen.HOME, "Inicio")
    object Monitoring : AppDestination(Screen.MONITORING, "Monitoreo")
    object Alert : AppDestination(Screen.ALERT, "Alerta")
    object SensorStatus : AppDestination(Screen.SENSOR_STATUS, "Estado de Sensores")
    object History : AppDestination(Screen.HISTORY, "Historial")
    object Settings : AppDestination(Screen.SETTINGS, "Configuración")
    object ReactionTest : AppDestination(Screen.REACTION_TEST, "Prueba de Reacción")
}
