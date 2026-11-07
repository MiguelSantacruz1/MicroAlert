package com.microalert.app.presentation.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microalert.app.data.repository.SensorRepository
import com.microalert.app.data.repository.SessionHistoryRepository
import com.microalert.app.domain.model.AlertEvent
import com.microalert.app.domain.model.AppMode
import com.microalert.app.domain.model.MonitoringSession
import com.microalert.app.domain.model.RiskLevel
import com.microalert.app.domain.usecase.EvaluateRiskUseCase
import com.microalert.app.domain.usecase.ExtractFeatureVectorUseCase
import com.microalert.app.domain.usecase.ManageAlertsUseCase
import com.microalert.app.domain.usecase.SaveSessionUseCase
import com.microalert.app.signalprocessing.SignalProcessingPipeline
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MonitoringViewModel(
    private val sensorRepository: SensorRepository? = null,
    private val signalProcessingPipeline: SignalProcessingPipeline? = null,
    private val extractFeatureVectorUseCase: ExtractFeatureVectorUseCase? = null,
    private val evaluateRiskUseCase: EvaluateRiskUseCase? = null,
    private val manageAlertsUseCase: ManageAlertsUseCase? = null,
    private val saveSessionUseCase: SaveSessionUseCase? = null,
    private val sessionHistoryRepository: SessionHistoryRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonitoringUiState())
    val uiState: StateFlow<MonitoringUiState> = _uiState.asStateFlow()

    private val _alertNavigationEvents = MutableSharedFlow<AlertEvent>(extraBufferCapacity = 5)
    val alertNavigationEvents: SharedFlow<AlertEvent> = _alertNavigationEvents.asSharedFlow()

    private var monitoringJob: Job? = null
    private var timerJob: Job? = null
    private var windowJob: Job? = null

    private var sessionStartTimestampMs: Long = 0L
    private var maxRiskObserved: RiskLevel = RiskLevel.NORMAL
    private var alertsCount: Int = 0
    private var sumHeartRate: Float = 0f
    private var countHeartRateSamples: Int = 0
    private var activeSessionId: Long = 0L

    fun startMonitoring(mode: AppMode) {
        if (_uiState.value.isMonitoring && sessionStartTimestampMs > 0) return

        sessionStartTimestampMs = System.currentTimeMillis()
        maxRiskObserved = RiskLevel.NORMAL
        alertsCount = 0
        sumHeartRate = 0f
        countHeartRateSamples = 0
        evaluateRiskUseCase?.reset()
        signalProcessingPipeline?.reset()

        _uiState.update {
            it.copy(
                isMonitoring = true,
                appMode = mode,
                sessionDurationSeconds = 0L,
                windowCountProcessed = 0,
                currentRiskLevel = RiskLevel.NORMAL
            )
        }

        timerJob = viewModelScope.launch {
            while (_uiState.value.isMonitoring) {
                delay(1000L)
                val durationSec = (System.currentTimeMillis() - sessionStartTimestampMs) / 1000L
                _uiState.update { it.copy(sessionDurationSeconds = durationSec) }
            }
        }

        sensorRepository?.startMonitoring(mode)

        sensorRepository?.let { repo ->
            monitoringJob = viewModelScope.launch {
                repo.getSensorDataStream().collect { sample ->
                    sample.heartRateBpm?.let { bpm ->
                        sumHeartRate += bpm
                        countHeartRateSamples++
                    }

                    _uiState.update { current ->
                        current.copy(
                            heartRateBpm = sample.heartRateBpm ?: current.heartRateBpm,
                            accelerometerMagnitude = sample.accelMagnitude,
                            gyroscopeMagnitude = sample.gyroMagnitude
                        )
                    }

                    signalProcessingPipeline?.processSample(sample)
                }
            }
        }

        signalProcessingPipeline?.let { pipeline ->
            windowJob = viewModelScope.launch {
                pipeline.windowStream.collect { windowData ->
                    val sessionDurationSec = ((System.currentTimeMillis() - sessionStartTimestampMs) / 1000f).coerceAtLeast(1f)

                    val featureVector = extractFeatureVectorUseCase?.invoke(
                        windowData = windowData,
                        sessionDurationSeconds = sessionDurationSec
                    )

                    if (featureVector != null && evaluateRiskUseCase != null) {
                        val riskResult = evaluateRiskUseCase.invoke(
                            featureVector = featureVector,
                            timestampMs = windowData.windowEndTimestampMs
                        )

                        if (riskResult.effectiveRiskLevel.ordinal > maxRiskObserved.ordinal) {
                            maxRiskObserved = riskResult.effectiveRiskLevel
                        }

                        _uiState.update { current ->
                            current.copy(
                                currentRiskLevel = riskResult.effectiveRiskLevel,
                                inactivityDurationSeconds = windowData.inactivitySeconds,
                                hrvAvailable = windowData.isHrvAvailable,
                                hrvSdnnMs = windowData.hrvSdnnMs,
                                hrvRmssdMs = windowData.hrvRmssdMs,
                                windowCountProcessed = current.windowCountProcessed + 1
                            )
                        }

                        if (manageAlertsUseCase != null) {
                            val alertEvent = manageAlertsUseCase.onRiskAssessed(
                                assessment = riskResult,
                                currentTimeMs = windowData.windowEndTimestampMs
                            )

                            if (alertEvent != null) {
                                alertsCount++
                                sessionHistoryRepository?.recordAlertForSession(activeSessionId, alertEvent)
                                _alertNavigationEvents.emit(alertEvent)
                            }
                        }
                    }
                }
            }
        }
    }

    fun stopMonitoring() {
        if (!_uiState.value.isMonitoring) return

        _uiState.update {
            it.copy(isMonitoring = false)
        }

        timerJob?.cancel()
        monitoringJob?.cancel()
        windowJob?.cancel()

        sensorRepository?.stopMonitoring()

        val endTimestampMs = System.currentTimeMillis()
        val durationSeconds = if (sessionStartTimestampMs > 0) {
            (endTimestampMs - sessionStartTimestampMs) / 1000L
        } else 0L

        val avgHeartRate = if (countHeartRateSamples > 0) {
            sumHeartRate / countHeartRateSamples
        } else null

        val finalSession = MonitoringSession(
            id = 0L,
            startTimestampMs = sessionStartTimestampMs,
            endTimestampMs = endTimestampMs,
            durationSeconds = durationSeconds,
            appMode = _uiState.value.appMode,
            maxRiskLevel = maxRiskObserved,
            alertsTriggeredCount = alertsCount,
            avgHeartRateBpm = avgHeartRate,
            processedWindowsCount = _uiState.value.windowCountProcessed
        )

        viewModelScope.launch {
            saveSessionUseCase?.invoke(finalSession)
        }
    }

    override fun onCleared() {
        stopMonitoring()
        super.onCleared()
    }
}
