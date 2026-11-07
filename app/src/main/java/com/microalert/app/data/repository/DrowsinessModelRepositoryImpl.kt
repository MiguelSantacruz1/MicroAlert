package com.microalert.app.data.repository

import com.microalert.app.domain.model.FeatureVector
import com.microalert.app.ml.ImputationStrategy
import com.microalert.app.ml.evaluator.RandomForestEvaluator
import com.microalert.app.ml.loader.ModelLoader
import com.microalert.app.ml.model.ModelMetadata
import com.microalert.app.ml.model.RandomForestModel

class DrowsinessModelRepositoryImpl(
    private val modelLoader: ModelLoader,
    private val evaluator: RandomForestEvaluator = RandomForestEvaluator(),
    private val imputationStrategy: ImputationStrategy = ImputationStrategy()
) : DrowsinessModelRepository {

    private val cachedModel: RandomForestModel by lazy {
        modelLoader.loadModel()
    }

    private val cachedMetadata: ModelMetadata by lazy {
        modelLoader.loadMetadata()
    }

    override fun getModel(): RandomForestModel = cachedModel

    override fun getMetadata(): ModelMetadata = cachedMetadata

    override fun predictDrowsinessProbability(featureVector: FeatureVector): Float {
        val imputedFeatures = imputationStrategy.imputeVector(featureVector)
        return evaluator.predictProbability(cachedModel, imputedFeatures)
    }
}
