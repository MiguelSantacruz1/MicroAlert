package com.microalert.app.ml

import com.microalert.app.domain.model.FeatureVector

class ImputationStrategy(
    val trainingMedians: FloatArray = floatArrayOf(
        70.45f,
        9.81f,
        0.04f,
        17.80f,
        357.55f,
        915.0f,
        49.46f,
        44.60f
    )
) {
    fun imputeVector(rawVector: FeatureVector): FloatArray {
        return rawVector.toFloatArray(trainingMedians)
    }
}
