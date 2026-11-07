package com.microalert.app.integration

import com.microalert.app.data.repository.DrowsinessModelRepository
import com.microalert.app.data.repository.SensorRepository
import com.microalert.app.data.repository.SessionHistoryRepository
import com.microalert.app.domain.alerts.AlertManager
import com.microalert.app.domain.classifier.RiskPolicyManager
import com.microalert.app.domain.model.AlertEvent
import com.microalert.app.domain.model.AppMode
import com.microalert.app.domain.model.FeatureVector
import com.microalert.app.domain.model.MonitoringSession
import com.microalert.app.domain.model.RiskLevel
import com.microalert.app.domain.usecase.EvaluateRiskUseCase
import com.microalert.app.domain.usecase.ExtractFeatureVectorUseCase
import com.microalert.app.domain.usecase.ManageAlertsUseCase
import com.microalert.app.domain.usecase.PredictDrowsinessProbabilityUseCase
import com.microalert.app.domain.usecase.SaveSessionUseCase
import com.microalert.app.presentation.monitoring.MonitoringViewModel
import com.microalert.app.sensors.model.SensorSample
import com.microalert.app.signalprocessing.FeatureVectorExtractor
import com.microalert.app.signalprocessing.ReactionTestManager
import com.microalert.app.signalprocessing.SignalProcessingPipeline
import com.microalert.app.signalprocessing.model.ProcessedWindowData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EndToEndMonitoringIntegrationTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeSensorRepository: FakeSensorRepository
    private lateinit var fakeSessionHistoryRepository: FakeSessionHistoryRepository
    private lateinit var fakeModelRepository: FakeModelRepository

    private lateinit var pipeline: SignalProcessingPipeline
    private lateinit var featureExtractor: FeatureVectorExtractor
    private lateinit var reactionTestManager: ReactionTestManager
    private lateinit var extractFeatureVectorUseCase: ExtractFeatureVectorUseCase
    private lateinit var predictProbabilityUseCase: PredictDrowsinessProbabilityUseCase
    private lateinit var riskPolicyManager: RiskPolicyManager
    private lateinit var evaluateRiskUseCase: EvaluateRiskUseCase
    private lateinit var alertManager: AlertManager
    private lateinit var manageAlertsUseCase: ManageAlertsUseCase
    private lateinit var saveSessionUseCase: SaveSessionUseCase

    private lateinit var viewModel: MonitoringViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        fakeSensorRepository = FakeSensorRepository()
        fakeSessionHistoryRepository = FakeSessionHistoryRepository()
        fakeModelRepository = FakeModelRepository()

        pipeline = SignalProcessingPipeline(windowDurationMs = 60_000L, windowStepMs = 30_000L)
        featureExtractor = FeatureVectorExtractor()
        reactionTestManager = ReactionTestManager()
        extractFeatureVectorUseCase = ExtractFeatureVectorUseCase(featureExtractor, reactionTestManager)

        predictProbabilityUseCase = PredictDrowsinessProbabilityUseCase(fakeModelRepository)
        riskPolicyManager = RiskPolicyManager()
        evaluateRiskUseCase = EvaluateRiskUseCase(predictProbabilityUseCase, riskPolicyManager)

        alertManager = AlertManager()
        manageAlertsUseCase = ManageAlertsUseCase(alertManager)
        saveSessionUseCase = SaveSessionUseCase(fakeSessionHistoryRepository)

        viewModel = MonitoringViewModel(
            sensorRepository = fakeSensorRepository,
            signalProcessingPipeline = pipeline,
            extractFeatureVectorUseCase = extractFeatureVectorUseCase,
            evaluateRiskUseCase = evaluateRiskUseCase,
            manageAlertsUseCase = manageAlertsUseCase,
            saveSessionUseCase = saveSessionUseCase,
            sessionHistoryRepository = fakeSessionHistoryRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `full monitoring cycle from sensor stream to window evaluation and session persistence`() = runTest {
        viewModel.startMonitoring(AppMode.DEMO)
        assertTrue(viewModel.uiState.value.isMonitoring)
        assertEquals(AppMode.DEMO, viewModel.uiState.value.appMode)

        fakeSensorRepository.emitSample(
            SensorSample(timestampMs = 1000L, heartRateBpm = 75f, accelMagnitude = 9.81f, gyroMagnitude = 0.05f)
        )
        fakeSensorRepository.emitSample(
            SensorSample(timestampMs = 31_000L, heartRateBpm = 72f, accelMagnitude = 9.80f, gyroMagnitude = 0.04f)
        )

        testScheduler.runCurrent()

        val currentState = viewModel.uiState.value
        assertEquals(72f, currentState.heartRateBpm, 0.1f)
        assertEquals(9.80f, currentState.accelerometerMagnitude, 0.05f)

        viewModel.stopMonitoring()
        testScheduler.runCurrent()

        val savedSessions = fakeSessionHistoryRepository.getAllSessions().first()
        assertEquals(1, savedSessions.size)
        val session = savedSessions.first()
        assertEquals(AppMode.DEMO, session.appMode)
        assertNotNull(session.avgHeartRateBpm)
    }

    private class FakeSensorRepository : SensorRepository {
        private val sensorFlow = MutableSharedFlow<SensorSample>(replay = 1, extraBufferCapacity = 10)

        suspend fun emitSample(sample: SensorSample) {
            sensorFlow.emit(sample)
        }

        override fun getSensorDataStream(): Flow<SensorSample> = sensorFlow
        override fun startMonitoring(appMode: AppMode) {}
        override fun stopMonitoring() {}
        override fun isHeartRateAvailable(): Boolean = true
        override fun isAccelerometerAvailable(): Boolean = true
        override fun isGyroscopeAvailable(): Boolean = true
        override fun isHrvAvailable(): Boolean = false
    }

    private class FakeModelRepository : DrowsinessModelRepository {
        override fun predictDrowsinessProbability(featureVector: FeatureVector): Float = 0.25f

        override fun getModel(): com.microalert.app.ml.model.RandomForestModel {
            return com.microalert.app.ml.model.RandomForestModel(
                version = "1.0.0",
                createdAt = "2026-11-06",
                modelType = "RandomForestClassifier",
                nEstimators = 0,
                featureNames = emptyList(),
                featureMedians = listOf(70.45f, 9.81f, 0.04f, 17.80f, 357.55f, 915.0f, 49.46f, 44.60f),
                rawTrees = emptyList()
            )
        }

        override fun getMetadata(): com.microalert.app.ml.model.ModelMetadata {
            return com.microalert.app.ml.model.ModelMetadata(
                version = "1.0.0",
                createdAt = "2026-11-06",
                featureNames = listOf(
                    "heart_rate", "accelerometer_magnitude", "gyroscope_magnitude",
                    "inactivity_duration_seconds", "reaction_time_ms", "session_duration_seconds",
                    "hrv_sdnn_ms", "hrv_rmssd_ms"
                ),
                featureMedians = listOf(70.45f, 9.81f, 0.04f, 17.80f, 357.55f, 915.0f, 49.46f, 44.60f)
            )
        }
    }

    private class FakeSessionHistoryRepository : SessionHistoryRepository {
        private val sessions = mutableListOf<MonitoringSession>()
        private val flow = MutableStateFlow<List<MonitoringSession>>(emptyList())

        override fun getAllSessions(): Flow<List<MonitoringSession>> = flow

        override suspend fun saveSession(session: MonitoringSession): Long {
            sessions.add(session)
            flow.value = sessions.toList()
            return session.id
        }

        override suspend fun recordAlertForSession(sessionId: Long, alert: AlertEvent): Long = 1L
        override suspend fun deleteSession(sessionId: Long) {}
        override suspend fun clearAllHistory() {
            sessions.clear()
            flow.value = emptyList()
        }
        override suspend fun getSessionCount(): Int = sessions.size
    }
}
