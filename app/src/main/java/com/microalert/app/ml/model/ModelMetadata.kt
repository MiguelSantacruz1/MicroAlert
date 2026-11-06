package com.microalert.app.ml.model

import com.google.gson.annotations.SerializedName

/**
 * Metadatos asociados al modelo Random Forest exportado.
 */
data class ModelMetadata(
    @SerializedName("version")
    val version: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("feature_names")
    val featureNames: List<String>,

    @SerializedName("feature_medians")
    val featureMedians: List<Float>,

    @SerializedName("equivalence_tolerance")
    val equivalenceTolerance: Double = 1e-6
)
