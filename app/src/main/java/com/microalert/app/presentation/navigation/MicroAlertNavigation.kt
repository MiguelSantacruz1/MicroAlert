package com.microalert.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.microalert.app.presentation.alert.AlertScreen
import com.microalert.app.presentation.alert.AlertUiState
import com.microalert.app.presentation.history.HistoryUiState
import com.microalert.app.presentation.history.SessionHistoryScreen
import com.microalert.app.presentation.home.HomeScreen
import com.microalert.app.presentation.home.HomeViewModel
import com.microalert.app.presentation.monitoring.MonitoringScreen
import com.microalert.app.presentation.monitoring.MonitoringViewModel
import com.microalert.app.presentation.reaction.ReactionTestScreen
import com.microalert.app.presentation.reaction.ReactionTestViewModel
import com.microalert.app.presentation.sensorstatus.SensorStatusScreen
import com.microalert.app.presentation.sensorstatus.SensorStatusUiState
import com.microalert.app.presentation.settings.SettingsScreen
import com.microalert.app.presentation.settings.SettingsUiState

object Screen {
    const val HOME = "home"
    const val MONITORING = "monitoring"
    const val ALERT = "alert"
    const val SENSOR_STATUS = "sensor_status"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val REACTION_TEST = "reaction_test"
}

/**
 * Grafo de Navegación completo para Wear OS.
 */
@Composable
fun MicroAlertNavHost(
    homeViewModel: HomeViewModel = viewModel(),
    monitoringViewModel: MonitoringViewModel = viewModel(),
    reactionTestViewModel: ReactionTestViewModel = viewModel()
) {
    val navController = rememberSwipeDismissableNavController()
    val homeUiState by homeViewModel.uiState.collectAsState()
    val monitoringUiState by monitoringViewModel.uiState.collectAsState()
    val reactionTestUiState by reactionTestViewModel.uiState.collectAsState()

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = Screen.HOME
    ) {
        composable(Screen.HOME) {
            HomeScreen(
                uiState = homeUiState,
                onStartMonitoringClick = {
                    homeViewModel.toggleMonitoring()
                    navController.navigate(Screen.MONITORING)
                },
                onNavigateToHistory = { navController.navigate(Screen.HISTORY) },
                onNavigateToSettings = { navController.navigate(Screen.SETTINGS) },
                onNavigateToSensorStatus = { navController.navigate(Screen.SENSOR_STATUS) },
                onToggleAppMode = { homeViewModel.toggleAppMode() }
            )
        }

        composable(Screen.MONITORING) {
            MonitoringScreen(
                uiState = monitoringUiState,
                onStopMonitoringClick = {
                    homeViewModel.toggleMonitoring()
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ALERT) {
            AlertScreen(
                uiState = AlertUiState(),
                onAcknowledgeAlert = { navController.popBackStack() }
            )
        }

        composable(Screen.SENSOR_STATUS) {
            SensorStatusScreen(uiState = SensorStatusUiState())
        }

        composable(Screen.HISTORY) {
            SessionHistoryScreen(
                uiState = HistoryUiState(),
                onClearHistoryClick = {}
            )
        }

        composable(Screen.SETTINGS) {
            SettingsScreen(
                uiState = SettingsUiState(),
                onToggleAppMode = { homeViewModel.toggleAppMode() },
                onToggleVibration = {},
                onToggleReactionTest = { navController.navigate(Screen.REACTION_TEST) }
            )
        }

        composable(Screen.REACTION_TEST) {
            ReactionTestScreen(
                uiState = reactionTestUiState,
                onStartTest = { reactionTestViewModel.startTestSequence() },
                onUserTap = { reactionTestViewModel.onUserReaction() },
                onFinish = {
                    reactionTestViewModel.reset()
                    navController.popBackStack()
                }
            )
        }
    }
}
