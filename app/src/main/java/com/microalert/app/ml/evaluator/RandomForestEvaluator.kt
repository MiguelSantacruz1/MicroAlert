package com.microalert.app.ml.evaluator

import com.microalert.app.ml.model.RandomForestModel

/**
 * Evaluador del ensamble Random Forest en Kotlin.
 * Calcula la probabilidad de somnolencia promediando las probabilidades emitidas por cada árbol.
 */
class RandomForestEvaluator(
    private val treeEvaluator: DecisionTreeEvaluator = DecisionTreeEvaluator()
) {
    fun predictProbability(model: RandomForestModel, features: FloatArray): Float {
        require(features.size == 8) {
            "El vector de características debe contener exactamente 8 variables. Tamaño recibido: ${features.size}"
        }

        val trees = model.decisionTrees
        check(trees.isNotEmpty()) {
            "El modelo Random Forest no contiene árboles de decisión cargados."
        }

        var sumProbabilities = 0.0
        for (tree in trees) {
            val treeProb = treeEvaluator.evaluate(tree, features)
            sumProbabilities += treeProb.toDouble()
        }

        val averageProbability = sumProbabilities / trees.size
        return averageProbability.toFloat()
    }
}
