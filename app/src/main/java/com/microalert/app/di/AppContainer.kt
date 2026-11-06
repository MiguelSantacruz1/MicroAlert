package com.microalert.app.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.microalert.app.data.repository.DrowsinessModelRepository
import com.microalert.app.data.repository.DrowsinessModelRepositoryImpl
import com.microalert.app.data.repository.SensorRepository
import com.microalert.app.data.repository.SensorRepositoryImpl
import com.microalert.app.data.repository.SessionHistoryRepository
import com.microalert.app.data.repository.SessionHistoryRepositoryImpl
import com.microalert.app.domain.alerts.AlertManager
import com.microalert.app.domain.classifier.RiskPolicyManager
import com.microalert.app.domain.usecase.CheckSensorAvailabilityUseCase
import com.microalert.app.domain.usecase.ClearHistoryUseCase
import com.microalert.app.domain.usecase.EvaluateRiskUseCase
import com.microalert.app.domain.usecase.ExtractFeatureVectorUseCase
import com.microalert.app.domain.usecase.GetSessionHistoryUseCase
import com.microalert.app.domain.usecase.ManageAlertsUseCase
import com.microalert.app.domain.usecase.ObserveSensorDataUseCase
import com.microalert.app.domain.usecase.PredictDrowsinessProbabilityUseCase
import com.microalert.app.domain.usecase.ProcessSignalWindowUseCase
import com.microalert.app.domain.usecase.SaveSessionUseCase
import com.microalert.app.ml.loader.AssetModelLoader
import com.microalert.app.ml.loader.ModelLoader
import com.microalert.app.presentation.alert.AlertViewModel
import com.microalert.app.presentation.history.SessionHistoryViewModel
import com.microalert.app.presentation.home.HomeViewModel
import com.microalert.app.presentation.monitoring.MonitoringViewModel
import com.microalert.app.presentation.reaction.ReactionTestViewModel
import com.microalert.app.presentation.sensorstatus.SensorStatusViewModel
import com.microalert.app.presentation.settings.SettingsViewModel
import com.microalert.app.storage.MicroAlertDatabase
import com.microalert.app.utils.HapticFeedbackManager

/**
 * Contenedor de dependencias manual (Service Locator) para toda la aplicación.
 * Garantiza desacoplamiento limpio, ciclo de vida único y testing modular.
 */
class AppContainer(private val context: Context) {

    val database: MicroAlertDatabase by lazy {
        MicroAlertDatabase.getInstance(context)
    }

    val sessionHistoryRepository: SessionHistoryRepository by lazy {
        SessionHistoryRepositoryImpl(database.sessionDao(), database.alertDao())
    }

    val modelLoader: ModelLoader by lazy {
        AssetModelLoader(context)
    }

    val modelRepository: DrowsinessModelRepository by lazy {
        DrowsinessModelRepositoryImpl(modelLoader)
    }

    val sensorRepository: SensorRepository by lazy {
        SensorRepositoryImpl(context)
    }

    val hapticFeedbackManager: HapticFeedbackManager by lazy {
        HapticFeedbackManager(context)
    }

    val alertManager: AlertManager by lazy {
        AlertManager(hapticFeedbackManager)
    }

    val riskPolicyManager: RiskPolicyManager by lazy {
        RiskPolicyManager()
    }

    val signalProcessingPipeline: com.microalert.app.signalprocessing.SignalProcessingPipeline by lazy {
        com.microalert.app.signalprocessing.SignalProcessingPipeline()
    }

    val featureVectorExtractor: com.microalert.app.signalprocessing.FeatureVectorExtractor by lazy {
        com.microalert.app.signalprocessing.FeatureVectorExtractor()
    }

    val reactionTestManager: com.microalert.app.signalprocessing.ReactionTestManager by lazy {
        com.microalert.app.signalprocessing.ReactionTestManager()
    }

    // Casos de uso
    val saveSessionUseCase: SaveSessionUseCase by lazy {
        SaveSessionUseCase(sessionHistoryRepository)
    }

    val getSessionHistoryUseCase: GetSessionHistoryUseCase by lazy {
        GetSessionHistoryUseCase(sessionHistoryRepository)
    }

    val clearHistoryUseCase: ClearHistoryUseCase by lazy {
        ClearHistoryUseCase(sessionHistoryRepository)
    }

    val predictDrowsinessProbabilityUseCase: PredictDrowsinessProbabilityUseCase by lazy {
        PredictDrowsinessProbabilityUseCase(modelRepository)
    }

    val evaluateRiskUseCase: EvaluateRiskUseCase by lazy {
        EvaluateRiskUseCase(predictDrowsinessProbabilityUseCase, riskPolicyManager)
    }

    val manageAlertsUseCase: ManageAlertsUseCase by lazy {
        ManageAlertsUseCase(alertManager)
    }

    val observeSensorDataUseCase: ObserveSensorDataUseCase by lazy {
        ObserveSensorDataUseCase(sensorRepository)
    }

    val checkSensorAvailabilityUseCase: CheckSensorAvailabilityUseCase by lazy {
        CheckSensorAvailabilityUseCase(sensorRepository)
    }

    val processSignalWindowUseCase: ProcessSignalWindowUseCase by lazy {
        ProcessSignalWindowUseCase(signalProcessingPipeline)
    }

    val extractFeatureVectorUseCase: ExtractFeatureVectorUseCase by lazy {
        ExtractFeatureVectorUseCase(featureVectorExtractor, reactionTestManager)
    }

    fun provideViewModelFactory(): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return when {
                modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                    HomeViewModel() as T
                }
                modelClass.isAssignableFrom(MonitoringViewModel::class.java) -> {
                    MonitoringViewModel(
                        sensorRepository = sensorRepository,
                        signalProcessingPipeline = signalProcessingPipeline,
                        extractFeatureVectorUseCase = extractFeatureVectorUseCase,
                        evaluateRiskUseCase = evaluateRiskUseCase,
                        manageAlertsUseCase = manageAlertsUseCase,
                        saveSessionUseCase = saveSessionUseCase,
                        sessionHistoryRepository = sessionHistoryRepository
                    ) as T
                }
                modelClass.isAssignableFrom(AlertViewModel::class.java) -> {
                    AlertViewModel(manageAlertsUseCase) as T
                }
                modelClass.isAssignableFrom(SessionHistoryViewModel::class.java) -> {
                    SessionHistoryViewModel(getSessionHistoryUseCase, clearHistoryUseCase) as T
                }
                modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                    SettingsViewModel(clearHistoryUseCase) as T
                }
                modelClass.isAssignableFrom(SensorStatusViewModel::class.java) -> {
                    SensorStatusViewModel(checkSensorAvailabilityUseCase) as T
                }
                modelClass.isAssignableFrom(ReactionTestViewModel::class.java) -> {
                    ReactionTestViewModel() as T
                }
                else -> throw IllegalArgumentException("ViewModel no reconocido: ${modelClass.name}")
            }
        }
    }
}
