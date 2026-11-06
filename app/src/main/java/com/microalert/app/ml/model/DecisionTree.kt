package com.microalert.app.ml.model

/**
 * Representa un árbol de decisión individual compuesto por una lista de nodos ordenados por su nodeId.
 */
data class DecisionTree(
    val nodes: List<DecisionTreeNode>
) {
    private val nodesMap: Map<Int, DecisionTreeNode> by lazy {
        nodes.associateBy { it.nodeId }
    }

    val rootNode: DecisionTreeNode?
        get() = nodesMap[0] ?: nodes.firstOrNull()

    fun getNode(nodeId: Int): DecisionTreeNode? = nodesMap[nodeId]
}
