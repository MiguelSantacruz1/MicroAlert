package com.microalert.app.ml

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.microalert.app.ml.evaluator.RandomForestEvaluator
import com.microalert.app.ml.loader.AssetModelLoader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import kotlin.math.abs

data class TestCasePayload(
    @SerializedName("id")
    val id: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("features")
    val features: List<Float>,
    @SerializedName("expected_probability")
    val expectedProbability: Float,
    @SerializedName("expected_drowsy_prediction")
    val expectedDrowsyPrediction: Int
)

data class TestVectorsPayload(
    @SerializedName("version")
    val version: String,
    @SerializedName("tolerance")
    val tolerance: Double,
    @SerializedName("feature_order")
    val featureOrder: List<String>,
    @SerializedName("test_cases")
    val testCases: List<TestCasePayload>
)

class ModelEquivalenceTest {

    private val evaluator = RandomForestEvaluator()
    private val gson = Gson()

    @Test
    fun `kotlin evaluator must reproduce python probabilities with tolerance less than 1e-6`() {
        val modelFile = File("src/main/assets/model.json").let {
            if (it.exists()) it else File("app/src/main/assets/model.json")
        }
        val metadataFile = File("src/main/assets/metadata.json").let {
            if (it.exists()) it else File("app/src/main/assets/metadata.json")
        }
        val testVectorsFile = File("src/main/assets/test_vectors.json").let {
            if (it.exists()) it else File("app/src/main/assets/test_vectors.json")
        }

        assertTrue("model.json debe existir", modelFile.exists())
        assertTrue("test_vectors.json debe existir", testVectorsFile.exists())

        val loader = AssetModelLoader(
            modelStreamProvider = { FileInputStream(modelFile) },
            metadataStreamProvider = { FileInputStream(metadataFile) }
        )
        val model = loader.loadModel()

        val testVectors: TestVectorsPayload = FileInputStream(testVectorsFile).use { fis ->
            InputStreamReader(fis, Charsets.UTF_8).use { reader ->
                gson.fromJson(reader, TestVectorsPayload::class.java)
            }
        }

        assertTrue("Debe contener casos de prueba", testVectors.testCases.isNotEmpty())

        val maxAllowedTolerance = 1e-6f

        println("=== INICIO PRUEBA DE EQUIVALENCIA PYTHON - KOTLIN ===")
        for (testCase in testVectors.testCases) {
            val featuresArray = testCase.features.toFloatArray()
            val actualProbability = evaluator.predictProbability(model, featuresArray)
            val expectedProbability = testCase.expectedProbability

            val difference = abs(actualProbability - expectedProbability)
            println("[${testCase.id}] Esperada (Python): $expectedProbability | Obtenida (Kotlin): $actualProbability | Diff: $difference")

            assertEquals(
                "Discrepancia superior a la tolerancia en ${testCase.id} (${testCase.description})",
                expectedProbability,
                actualProbability,
                maxAllowedTolerance
            )
        }
        println("=== PRUEBA DE EQUIVALENCIA EXITOSA: 100% CONFORMIDAD ===")
    }
}
