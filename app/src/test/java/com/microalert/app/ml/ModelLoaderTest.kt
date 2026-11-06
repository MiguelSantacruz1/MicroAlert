package com.microalert.app.ml

import com.microalert.app.ml.loader.AssetModelLoader
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.io.FileInputStream

class ModelLoaderTest {

    private fun createLoader(): AssetModelLoader {
        // En tests unitarios de Gradle/JVM, los assets se localizan en el path del proyecto
        val modelFile = File("src/main/assets/model.json").let {
            if (it.exists()) it else File("app/src/main/assets/model.json")
        }
        val metadataFile = File("src/main/assets/metadata.json").let {
            if (it.exists()) it else File("app/src/main/assets/metadata.json")
        }

        assertTrue("model.json no existe en ${modelFile.absolutePath}", modelFile.exists())
        assertTrue("metadata.json no existe en ${metadataFile.absolutePath}", metadataFile.exists())

        return AssetModelLoader(
            modelStreamProvider = { FileInputStream(modelFile) },
            metadataStreamProvider = { FileInputStream(metadataFile) }
        )
    }

    @Test
    fun `loadMetadata should deserialize valid metadata with 8 features`() {
        val loader = createLoader()
        val metadata = loader.loadMetadata()

        assertEquals("1.0.0", metadata.version)
        assertEquals(8, metadata.featureNames.size)
        assertEquals(8, metadata.featureMedians.size)
        assertEquals("heartRate", metadata.featureNames[0])
        assertEquals("hrvRmssdMs", metadata.featureNames[7])
    }

    @Test
    fun `loadModel should deserialize 25 trees and valid structure`() {
        val loader = createLoader()
        val model = loader.loadModel()

        assertEquals("1.0.0", model.version)
        assertEquals(25, model.nEstimators)
        assertEquals(25, model.rawTrees.size)
        assertEquals(25, model.decisionTrees.size)

        val firstTree = model.decisionTrees[0]
        assertNotNull(firstTree.rootNode)
        assertTrue(firstTree.nodes.size > 1)
    }
}
