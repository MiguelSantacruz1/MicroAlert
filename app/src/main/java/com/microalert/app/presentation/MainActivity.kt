package com.microalert.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.microalert.app.presentation.navigation.MicroAlertNavHost
import com.microalert.app.presentation.theme.MicroAlertTheme

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
