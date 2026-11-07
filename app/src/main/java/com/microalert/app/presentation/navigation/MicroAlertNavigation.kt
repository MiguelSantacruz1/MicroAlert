package com.microalert.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.microalert.app.MicroAlertApp
import com.microalert.app.presentation.alert.AlertScreen
import com.microalert.app.presentation.alert.AlertViewModel
import com.microalert.app.presentation.history.SessionHistoryScreen
import com.microalert.app.presentation.history.SessionHistoryViewModel
import com.microalert.app.presentation.home.HomeScreen
import com.microalert.app.presentation.home.HomeViewModel
import com.microalert.app.presentation.monitoring.MonitoringScreen
import com.microalert.app.presentation.monitoring.MonitoringViewModel
import com.microalert.app.presentation.reaction.ReactionTestScreen
import com.microalert.app.presentation.reaction.ReactionTestViewModel
import com.microalert.app.presentation.sensorstatus.SensorStatusScreen
import com.microalert.app.presentation.sensorstatus.SensorStatusViewModel
import com.microalert.app.presentation.settings.SettingsScreen
import com.microalert.app.presentation.settings.SettingsViewModel
import com.microalert.app.services.MonitoringForegroundService

object Screen {
    const val HOME = "home"
    const val MONITORING = "monitoring"
    const val ALERT = "alert"
    const val SENSOR_STATUS = "sensor_status"
    const val HISTORY = "history"
    const val SETTINGS = "settings"
    const val REACTION_TEST = "reaction_test"
}

@Composable
fun MicroAlertNavHost(
    factory: ViewModelProvider.Factory = (LocalContext.current.applicationContext as MicroAlertApp).appContainer.provideViewModelFactory(),
    homeViewModel: HomeViewModel = viewModel(factory = factory),
    monitoringViewModel: MonitoringViewModel = viewModel(factory = factory),
    reactionTestViewModel: ReactionTestViewModel = viewModel(factory = factory),
    alertViewModel: AlertViewModel = viewModel(factory = factory),
    historyViewModel: SessionHistoryViewModel = viewModel(factory = factory),
    settingsViewModel: SettingsViewModel = viewModel(factory = factory),
    sensorStatusViewModel: SensorStatusViewModel = viewModel(factory = factory)
) {
    val navController = rememberSwipeDismissableNavController()
    val homeUiState by homeViewModel.uiState.collectAsState()
    val monitoringUiState by monitoringViewModel.uiState.collectAsState()
    val reactionTestUiState by reactionTestViewModel.uiState.collectAsState()
    val alertUiState by alertViewModel.uiState.collectAsState()
    val historyUiState by historyViewModel.uiState.collectAsState()
    val settingsUiState by settingsViewModel.uiState.collectAsState()
    val sensorStatusUiState by sensorStatusViewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(monitoringViewModel) {
        monitoringViewModel.alertNavigationEvents.collect { alertEvent ->
            alertViewModel.onAlertTriggered(alertEvent)
            navController.navigate(Screen.ALERT)
        }
    }

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = Screen.HOME
    ) {
        composable(Screen.HOME) {
            HomeScreen(
                uiState = homeUiState,
                onStartMonitoringClick = {
                    homeViewModel.toggleMonitoring()
                    monitoringViewModel.startMonitoring(homeUiState.appMode)
                    MonitoringForegroundService.startService(context)
                    navController.navigate(Screen.MONITORING)
                },
                onNavigateToHistory = { navController.navigate(Screen.HISTORY) },
                onNavigateToSettings = { navController.navigate(Screen.SETTINGS) },
                onNavigateToSensorStatus = { navController.navigate(Screen.SENSOR_STATUS) },
                onToggleAppMode = {
                    homeViewModel.toggleAppMode()
                    settingsViewModel.toggleAppMode()
                }
            )
        }

        composable(Screen.MONITORING) {
            MonitoringScreen(
                uiState = monitoringUiState,
                onStopMonitoringClick = {
                    homeViewModel.toggleMonitoring()
                    monitoringViewModel.stopMonitoring()
                    MonitoringForegroundService.stopService(context)
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ALERT) {
            AlertScreen(
                uiState = alertUiState,
                onAcknowledgeAlert = {
                    alertViewModel.acknowledgeAlert()
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.SENSOR_STATUS) {
            SensorStatusScreen(uiState = sensorStatusUiState)
        }

        composable(Screen.HISTORY) {
            SessionHistoryScreen(
                uiState = historyUiState,
                onClearHistoryClick = { historyViewModel.clearHistory() }
            )
        }

        composable(Screen.SETTINGS) {
            SettingsScreen(
                uiState = settingsUiState,
                onToggleAppMode = {
                    settingsViewModel.toggleAppMode()
                    homeViewModel.toggleAppMode()
                },
                onToggleVibration = { settingsViewModel.toggleVibration() },
                onToggleReactionTest = { navController.navigate(Screen.REACTION_TEST) },
                onCycleCooldown = { settingsViewModel.cycleCooldown() },
                onClearAllData = {
                    settingsViewModel.clearAllData()
                    historyViewModel.clearHistory()
                }
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
