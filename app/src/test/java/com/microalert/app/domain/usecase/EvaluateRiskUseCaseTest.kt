package com.microalert.app.domain.usecase

import com.microalert.app.data.repository.DrowsinessModelRepositoryImpl
import com.microalert.app.domain.classifier.RiskPolicyManager
import com.microalert.app.domain.model.FeatureVector
import com.microalert.app.domain.model.RiskClassificationConfig
import com.microalert.app.domain.model.RiskLevel
import com.microalert.app.ml.loader.AssetModelLoader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.io.FileInputStream

class EvaluateRiskUseCaseTest {

    private fun createUseCase(persistenceRequired: Int = 1): EvaluateRiskUseCase {
        val modelFile = File("src/main/assets/model.json").let {
            if (it.exists()) it else File("app/src/main/assets/model.json")
        }
        val metadataFile = File("src/main/assets/metadata.json").let {
            if (it.exists()) it else File("app/src/main/assets/metadata.json")
        }

        val loader = AssetModelLoader(
            modelStreamProvider = { FileInputStream(modelFile) },
            metadataStreamProvider = { FileInputStream(metadataFile) }
        )
        val repo = DrowsinessModelRepositoryImpl(modelLoader = loader)
        val predictUseCase = PredictDrowsinessProbabilityUseCase(repo)
        val policyManager = RiskPolicyManager(
            config = RiskClassificationConfig(
                persistenceWindowsRequired = persistenceRequired
            )
        )
        return EvaluateRiskUseCase(predictUseCase, policyManager)
    }

    @Test
    fun `evaluating alert vector should yield NORMAL risk without triggering alerts`() {
        val useCase = createUseCase()
        val alertVector = FeatureVector(
            heartRate = 82f,
            accelerometerMagnitude = 10.2f,
            gyroscopeMagnitude = 0.25f,
            inactivityDurationSeconds = 5f,
            reactionTimeMs = 260f,
            sessionDurationSeconds = 120f,
            hrvSdnnMs = 36f,
            hrvRmssdMs = 32f
        )

        val result = useCase(alertVector)

        assertEquals(RiskLevel.NORMAL, result.effectiveRiskLevel)
        assertFalse(result.isAlertTriggered)
        assertTrue(result.drowsinessProbability < 0.10f)
        assertEquals(8, result.dataQuality.realFeaturesCount)
        assertEquals(100.0f, result.dataQuality.qualityPercentage, 1e-4f)
    }

    @Test
    fun `evaluating drowsy vector with missing HRV should correctly report quality and detect risk`() {
        val useCase = createUseCase()
        val drowsyVector = FeatureVector(
            heartRate = 58f,
            accelerometerMagnitude = 9.81f,
            gyroscopeMagnitude = 0.01f,
            inactivityDurationSeconds = 52f,
            reactionTimeMs = 680f,
            sessionDurationSeconds = 1500f,
            hrvSdnnMs = null, // Imputado
            hrvRmssdMs = null // Imputado
        )

        val result = useCase(drowsyVector)

        assertTrue(result.drowsinessProbability > 0.85f)
        assertEquals(RiskLevel.CRITICAL, result.effectiveRiskLevel)
        assertTrue(result.isAlertTriggered)

        // Verificación de transparencia de datos
        assertFalse(result.dataQuality.isHrvAvailable)
        assertEquals(6, result.dataQuality.realFeaturesCount)
        assertEquals(75.0f, result.dataQuality.qualityPercentage, 1e-4f)
    }
}
