package com.microalert.app.ml

import com.google.gson.JsonParser
import com.microalert.app.ml.predictor.RiskForecaster
import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RiskForecasterTest {

    private val file = File("src/main/assets/predictor.json")

    private fun forecaster() = file.inputStream().use { RiskForecaster.fromStream(it) }

    @Test
    fun `coincide con los vectores de prueba de scikit-learn`() {
        val json = JsonParser.parseString(file.readText()).asJsonObject
        val vectors = json.getAsJsonArray("test_vectors")
        val f = forecaster()
        for (v in vectors) {
            val o = v.asJsonObject
            val x = o.getAsJsonArray("x").map { it.asFloat }
            val expected = o.get("p").asFloat
            val actual = f.predict(x.take(f.lag))
            assertEquals(expected, actual, 0.01f)
        }
    }

    @Test
    fun `no predice hasta tener la ventana completa`() {
        val f = forecaster()
        repeat(f.lag - 1) { assertNull(f.update(0.1f)) }
        assertTrue(f.update(0.1f) != null)
    }

    @Test
    fun `riesgo subiendo da mas probabilidad que riesgo estable`() {
        val f = forecaster()
        val rising = List(f.lag) { 0.15f + it * 0.04f }
        val flat = List(f.lag) { 0.05f }
        assertTrue(f.predict(rising) > f.predict(flat))
    }
}
