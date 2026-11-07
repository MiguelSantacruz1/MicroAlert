package com.microalert.app.ml.evaluator

import com.microalert.app.ml.model.DecisionTree
import com.microalert.app.ml.model.DecisionTreeNode

class DecisionTreeEvaluator {

    fun evaluate(tree: DecisionTree, features: FloatArray): Float {
        var currentNode: DecisionTreeNode? = tree.rootNode
            ?: throw IllegalStateException("El árbol de decisión no contiene nodo raíz.")

        while (currentNode != null) {
            if (currentNode.isLeaf) {
                return currentNode.value ?: 0.0f
            }

            val featureIndex = currentNode.featureIndex
            require(featureIndex in features.indices) {
                "Índice de característica inválido: $featureIndex (tamaño del vector: ${features.size})"
            }

            val featureValue = features[featureIndex]
            val threshold = currentNode.threshold ?: 0.0f

            val nextNodeId = if (featureValue <= threshold) {
                currentNode.leftChild
            } else {
                currentNode.rightChild
            }

            currentNode = tree.getNode(nextNodeId)
                ?: throw IllegalStateException("Nodo hijo no encontrado con ID: $nextNodeId")
        }

        return 0.0f
    }
}
