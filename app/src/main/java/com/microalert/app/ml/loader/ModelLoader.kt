package com.microalert.app.ml.loader

import com.microalert.app.ml.model.ModelMetadata
import com.microalert.app.ml.model.RandomForestModel

interface ModelLoader {
    fun loadModel(): RandomForestModel
    fun loadMetadata(): ModelMetadata
}
