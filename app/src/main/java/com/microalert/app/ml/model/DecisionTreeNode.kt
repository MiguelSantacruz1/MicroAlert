package com.microalert.app.ml.model

import com.google.gson.annotations.SerializedName

/**
 * Representa un nodo individual en un árbol de decisión exportado.
 * Si leftChild == -1 y rightChild == -1, es un nodo hoja con probabilidad 'value'.
 */
data class DecisionTreeNode(
    @SerializedName("node_id")
    val nodeId: Int,

    @SerializedName("feature_index")
    val featureIndex: Int,

    @SerializedName("threshold")
    val threshold: Float?,

    @SerializedName("left_child")
    val leftChild: Int,

    @SerializedName("right_child")
    val rightChild: Int,

    @SerializedName("value")
    val value: Float?
) {
    val isLeaf: Boolean
        get() = leftChild == -1 && rightChild == -1
}
