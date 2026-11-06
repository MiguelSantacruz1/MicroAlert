package com.microalert.app.ml.model

import com.google.gson.annotations.SerializedName

/**
 * Representa el modelo completo de Random Forest deserializado a partir de model.json.
 */
data class RandomForestModel(
    @SerializedName("version")
    val version: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("model_type")
    val modelType: String,

    @SerializedName("n_estimators")
    val nEstimators: Int,

    @SerializedName("feature_names")
    val featureNames: List<String>,

    @SerializedName("feature_medians")
    val featureMedians: List<Float>,

    @SerializedName("trees")
    val rawTrees: List<List<DecisionTreeNode>>
) {
    val decisionTrees: List<DecisionTree> by lazy {
        rawTrees.map { DecisionTree(it) }
    }
}
