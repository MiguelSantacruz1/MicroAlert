package com.microalert.app.ml.model

import com.google.gson.annotations.SerializedName

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
    @Transient
    private var cachedTrees: List<DecisionTree>? = null

    val decisionTrees: List<DecisionTree>
        get() {
            var trees = cachedTrees
            if (trees == null) {
                trees = rawTrees.map { DecisionTree(it) }
                cachedTrees = trees
            }
            return trees
        }
}
