package com.microalert.app.ml.loader

import com.microalert.app.ml.model.ModelMetadata
import com.microalert.app.ml.model.RandomForestModel

/**
 * Contrato para la carga y deserialización de modelos de Machine Learning.
 */
interface ModelLoader {
    fun loadModel(): RandomForestModel
    fun loadMetadata(): ModelMetadata
}
