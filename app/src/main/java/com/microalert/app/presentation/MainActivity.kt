package com.microalert.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.microalert.app.presentation.navigation.MicroAlertNavHost
import com.microalert.app.presentation.theme.MicroAlertTheme

/**
 * Actividad principal de MICROALERT para Wear OS.
 * Integra la navegación completa y el tema Material 3 for Wear OS.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MicroAlertTheme {
                MicroAlertNavHost()
            }
        }
    }
}
