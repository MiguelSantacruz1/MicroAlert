package com.microalert.app.ml

import com.microalert.app.ml.evaluator.DecisionTreeEvaluator
import com.microalert.app.ml.model.DecisionTree
import com.microalert.app.ml.model.DecisionTreeNode
import org.junit.Assert.assertEquals
import org.junit.Test

class DecisionTreeEvaluatorTest {

    private val evaluator = DecisionTreeEvaluator()

    @Test
    fun `evaluator should return leaf value immediately when root is leaf`() {
        val singleLeafTree = DecisionTree(
            listOf(
                DecisionTreeNode(nodeId = 0, featureIndex = -1, threshold = null, leftChild = -1, rightChild = -1, value = 0.85f)
            )
        )

        val features = FloatArray(8) { 0f }
        val result = evaluator.evaluate(singleLeafTree, features)

        assertEquals(0.85f, result, 1e-6f)
    }

    @Test
    fun `evaluator should follow left branch when feature is less than or equal to threshold`() {
        // Árbol simple: Si x[0] <= 65.0 -> Hoja(0.10) sino -> Hoja(0.90)
        val tree = DecisionTree(
            listOf(
                DecisionTreeNode(nodeId = 0, featureIndex = 0, threshold = 65.0f, leftChild = 1, rightChild = 2, value = null),
                DecisionTreeNode(nodeId = 1, featureIndex = -1, threshold = null, leftChild = -1, rightChild = -1, value = 0.10f),
                DecisionTreeNode(nodeId = 2, featureIndex = -1, threshold = null, leftChild = -1, rightChild = -1, value = 0.90f)
            )
        )

        val featuresLeft = floatArrayOf(60.0f, 9.8f, 0.05f, 10f, 300f, 200f, 40f, 35f)
        val resultLeft = evaluator.evaluate(tree, featuresLeft)
        assertEquals(0.10f, resultLeft, 1e-6f)

        val featuresRight = floatArrayOf(75.0f, 9.8f, 0.05f, 10f, 300f, 200f, 40f, 35f)
        val resultRight = evaluator.evaluate(tree, featuresRight)
        assertEquals(0.90f, resultRight, 1e-6f)
    }
}
