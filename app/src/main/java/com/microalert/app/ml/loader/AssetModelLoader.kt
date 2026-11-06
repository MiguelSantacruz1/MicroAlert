package com.microalert.app.ml.loader

import android.content.Context
import com.google.gson.Gson
import com.microalert.app.ml.model.ModelMetadata
import com.microalert.app.ml.model.RandomForestModel
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Cargador de modelos desde assets empaquetados en la aplicación o desde flujos de entrada en pruebas.
 */
class AssetModelLoader(
    private val modelStreamProvider: () -> InputStream,
    private val metadataStreamProvider: () -> InputStream,
    private val gson: Gson = Gson()
) : ModelLoader {

    constructor(
        context: Context,
        modelFileName: String = "model.json",
        metadataFileName: String = "metadata.json",
        gson: Gson = Gson()
    ) : this(
        modelStreamProvider = { context.assets.open(modelFileName) },
        metadataStreamProvider = { context.assets.open(metadataFileName) },
        gson = gson
    )

    override fun loadModel(): RandomForestModel {
        modelStreamProvider().use { inputStream ->
            InputStreamReader(inputStream, Charsets.UTF_8).use { reader ->
                return gson.fromJson(reader, RandomForestModel::class.java)
                    ?: throw IllegalStateException("Error al deserializar model.json")
            }
        }
    }

    override fun loadMetadata(): ModelMetadata {
        metadataStreamProvider().use { inputStream ->
            InputStreamReader(inputStream, Charsets.UTF_8).use { reader ->
                return gson.fromJson(reader, ModelMetadata::class.java)
                    ?: throw IllegalStateException("Error al deserializar metadata.json")
            }
        }
    }
}
