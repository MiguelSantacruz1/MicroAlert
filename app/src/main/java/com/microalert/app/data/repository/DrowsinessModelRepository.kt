package com.microalert.app.data.repository

import com.microalert.app.domain.model.FeatureVector
import com.microalert.app.ml.model.ModelMetadata
import com.microalert.app.ml.model.RandomForestModel

/**
 * Repositorio para la gestión del modelo de Machine Learning y ejecución de predicciones.
 */
interface DrowsinessModelRepository {
    fun getModel(): RandomForestModel
    fun getMetadata(): ModelMetadata
    fun predictDrowsinessProbability(featureVector: FeatureVector): Float
}
