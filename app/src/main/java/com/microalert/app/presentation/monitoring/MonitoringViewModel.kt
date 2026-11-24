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
import com.microalert.app.domain.classifier.PersonalCalibrator
import com.microalert.app.ml.predictor.RiskForecaster
import com.microalert.app.sensors.DemoLevel
import com.microalert.app.sensors.DemoScenario
import com.microalert.app.signalprocessing.ReactionTestManager
import com.microalert.app.signalprocessing.SignalProcessingPipeline
import com.microalert.app.signalprocessing.model.ReactionTestResult
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
    private val sessionHistoryRepository: SessionHistoryRepository? = null,
    private val reactionTestManager: ReactionTestManager? = null,
    private val riskForecaster: RiskForecaster? = null,
    private val calibrator: PersonalCalibrator = PersonalCalibrator()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MonitoringUiState())
    val uiState: StateFlow<MonitoringUiState> = _uiState.asStateFlow()

    private val _alertNavigationEvents = MutableSharedFlow<AlertEvent>(extraBufferCapacity = 5)
    val alertNavigationEvents: SharedFlow<AlertEvent> = _alertNavigationEvents.asSharedFlow()

    private var monitoringJob: Job? = null
    private var timerJob: Job? = null
    private var windowJob: Job? = null
    private var sessionIdJob: Job? = null

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
        manageAlertsUseCase?.reset()
        riskForecaster?.reset()
        calibrator.reset()
        signalProcessingPipeline?.restoreDefaults()
        if (mode == AppMode.DEMO) {
            signalProcessingPipeline?.configure(DemoScenario.WINDOW_MS, DemoScenario.STEP_MS)
            DemoScenario.start()
        }
        signalProcessingPipeline?.reset()

        val initialSession = MonitoringSession(
            id = 0L,
            startTimestampMs = sessionStartTimestampMs,
            endTimestampMs = sessionStartTimestampMs,
            durationSeconds = 0L,
            appMode = mode,
            maxRiskLevel = RiskLevel.NORMAL,
            alertsTriggeredCount = 0,
            avgHeartRateBpm = null,
            processedWindowsCount = 0
        )

        activeSessionId = 0L
        sessionIdJob = viewModelScope.launch {
            activeSessionId = saveSessionUseCase?.invoke(initialSession) ?: 0L
        }

        _uiState.update {
            it.copy(
                isMonitoring = true,
                appMode = mode,
                sessionDurationSeconds = 0L,
                windowCountProcessed = 0,
                demoLevel = DemoLevel.AUTO,
                calibrationProgress = 0f,
                baselineHeartRate = null,
                thresholdOffset = 0f,
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
                            heartRateValid = sample.heartRateBpm != null,
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
                pipeline.windowStream.collect { rawWindow ->
                    val elapsedMs = System.currentTimeMillis() - sessionStartTimestampMs
                    var sessionDurationSec = (elapsedMs / 1000f).coerceAtLeast(1f)
                    var windowData = rawWindow
                    if (_uiState.value.appMode == AppMode.DEMO) {
                        val span = rawWindow.windowEndTimestampMs - rawWindow.windowStartTimestampMs
                        if (span < DemoScenario.WINDOW_MS * 7 / 10) return@collect
                        val fatigue = DemoScenario.current
                        sessionDurationSec = DemoScenario.sessionSeconds(fatigue)
                        windowData = rawWindow.copy(
                            inactivitySeconds = (rawWindow.inactivitySeconds * DemoScenario.INACTIVITY_SCALE).toLong()
                        )
                        reactionTestManager?.recordResult(
                            ReactionTestResult(
                                latencyMs = DemoScenario.reactionTimeMs(fatigue),
                                isSuccessful = true,
                                isTimedOut = false
                            )
                        )
                    }

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

                        if (!calibrator.isCalibrated) {
                            calibrator.observe(riskResult.drowsinessProbability, featureVector.heartRate)
                            if (calibrator.isCalibrated) {
                                evaluateRiskUseCase.setThresholdOffset(calibrator.thresholdOffset)
                            }
                        }

                        val forecast = riskForecaster?.update(riskResult.drowsinessProbability)
                            ?.takeIf { riskResult.effectiveRiskLevel.ordinal < RiskLevel.HIGH.ordinal }

                        _uiState.update { current ->
                            current.copy(
                                calibrationProgress = if (calibrator.isCalibrated) null else calibrator.progress,
                                baselineHeartRate = calibrator.baselineHeartRate,
                                thresholdOffset = calibrator.thresholdOffset,
                                forecastProbability = forecast,
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
                                sessionIdJob?.join()
                                if (activeSessionId > 0L) {
                                    try {
                                        sessionHistoryRepository?.recordAlertForSession(activeSessionId, alertEvent)
                                    } catch (_: Exception) {
                                    }
                                }
                                _alertNavigationEvents.emit(alertEvent)
                            }
                        }
                    }
                }
            }
        }
    }

    fun setDemoLevel(level: DemoLevel) {
        DemoScenario.level = level
        if (level == DemoLevel.NORMAL) {
            DemoScenario.current = 0f
            signalProcessingPipeline?.reset()
            evaluateRiskUseCase?.reset()
            manageAlertsUseCase?.reset()
            riskForecaster?.reset()
            _uiState.update { it.copy(demoLevel = level, currentRiskLevel = RiskLevel.NORMAL, forecastProbability = null) }
        } else {
            _uiState.update { it.copy(demoLevel = level) }
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
            id = activeSessionId,
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
