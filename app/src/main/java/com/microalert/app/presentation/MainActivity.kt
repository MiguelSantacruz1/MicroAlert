package com.microalert.app.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.microalert.app.MicroAlertApp
import com.microalert.app.presentation.navigation.MicroAlertNavHost
import com.microalert.app.presentation.theme.MicroAlertTheme

class MainActivity : ComponentActivity() {

    private var permissionsVersion by mutableIntStateOf(0)

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        permissionsVersion++
    }

    private fun requestMissingPermissions() {
        val permissionManager = (application as MicroAlertApp).appContainer.permissionManager
        val missing = permissionManager.getMissingPermissions()
        if (missing.isEmpty()) {
            permissionsVersion++
        } else {
            permissionLauncher.launch(missing.toTypedArray())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MicroAlertTheme {
                MicroAlertNavHost(
                    permissionsVersion = permissionsVersion,
                    onRequestPermissions = ::requestMissingPermissions
                )
            }
        }
    }
}
