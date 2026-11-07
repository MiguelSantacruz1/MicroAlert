package com.microalert.app.ml

import com.microalert.app.ml.evaluator.RandomForestEvaluator
import com.microalert.app.ml.model.DecisionTreeNode
import com.microalert.app.ml.model.RandomForestModel
import org.junit.Assert.assertEquals
import org.junit.Test

class RandomForestEvaluatorTest {

    private val evaluator = RandomForestEvaluator()

    @Test
    fun `predictProbability should average probabilities across all trees`() {
        val tree1Nodes = listOf(
            DecisionTreeNode(nodeId = 0, featureIndex = -1, threshold = null, leftChild = -1, rightChild = -1, value = 0.20f)
        )
        val tree2Nodes = listOf(
            DecisionTreeNode(nodeId = 0, featureIndex = -1, threshold = null, leftChild = -1, rightChild = -1, value = 0.80f)
        )

        val model = RandomForestModel(
            version = "1.0.0",
            createdAt = "2026-11-06T00:00:00Z",
            modelType = "RandomForestClassifier",
            nEstimators = 2,
            featureNames = listOf("f0", "f1", "f2", "f3", "f4", "f5", "f6", "f7"),
            featureMedians = listOf(70f, 9.8f, 0.05f, 15f, 350f, 600f, 45f, 40f),
            rawTrees = listOf(tree1Nodes, tree2Nodes)
        )

        val features = FloatArray(8) { 10f }
        val probability = evaluator.predictProbability(model, features)

        assertEquals(0.50f, probability, 1e-6f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `predictProbability should throw exception when feature array size is not 8`() {
        val model = RandomForestModel(
            version = "1.0.0",
            createdAt = "2026-11-06T00:00:00Z",
            modelType = "RandomForestClassifier",
            nEstimators = 1,
            featureNames = emptyList(),
            featureMedians = emptyList(),
            rawTrees = listOf(listOf(DecisionTreeNode(0, -1, null, -1, -1, 0.5f)))
        )

        evaluator.predictProbability(model, FloatArray(7))
    }
}
