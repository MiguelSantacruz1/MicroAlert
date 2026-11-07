package com.microalert.app.domain.usecase

import com.microalert.app.data.repository.DrowsinessModelRepositoryImpl
import com.microalert.app.domain.model.FeatureVector
import com.microalert.app.ml.loader.AssetModelLoader
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.io.FileInputStream

class PredictDrowsinessProbabilityUseCaseTest {

    private fun createUseCase(): PredictDrowsinessProbabilityUseCase {
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
        val repository = DrowsinessModelRepositoryImpl(modelLoader = loader)
        return PredictDrowsinessProbabilityUseCase(modelRepository = repository)
    }

    @Test
    fun `use case should return low drowsiness probability for active alert vector`() {
        val useCase = createUseCase()
        val alertVector = FeatureVector(
            heartRate = 80f,
            accelerometerMagnitude = 10.1f,
            gyroscopeMagnitude = 0.2f,
            inactivityDurationSeconds = 5f,
            reactionTimeMs = 280f,
            sessionDurationSeconds = 120f,
            hrvSdnnMs = 35f,
            hrvRmssdMs = 30f
        )

        val prob = useCase(alertVector)
        assertTrue("La probabilidad de somnolencia debe ser baja (< 0.15), obtenida: $prob", prob < 0.15f)
    }

    @Test
    fun `use case should return high drowsiness probability for drowsy pattern`() {
        val useCase = createUseCase()
        val drowsyVector = FeatureVector(
            heartRate = 56f,
            accelerometerMagnitude = 9.81f,
            gyroscopeMagnitude = 0.01f,
            inactivityDurationSeconds = 55f,
            reactionTimeMs = 700f,
            sessionDurationSeconds = 1500f,
            hrvSdnnMs = 75f,
            hrvRmssdMs = 70f
        )

        val prob = useCase(drowsyVector)
        assertTrue("La probabilidad de somnolencia debe ser alta (> 0.85), obtenida: $prob", prob > 0.85f)
    }

    @Test
    fun `use case should handle missing HRV and reaction time with imputation`() {
        val useCase = createUseCase()
        val vectorWithNulls = FeatureVector(
            heartRate = 72f,
            accelerometerMagnitude = 9.81f,
            gyroscopeMagnitude = 0.05f,
            inactivityDurationSeconds = 15f,
            reactionTimeMs = null,
            sessionDurationSeconds = 300f,
            hrvSdnnMs = null,
            hrvRmssdMs = null
        )

        val prob = useCase(vectorWithNulls)
        assertTrue("La probabilidad debe estar en [0, 1]", prob in 0.0f..1.0f)
    }
}
