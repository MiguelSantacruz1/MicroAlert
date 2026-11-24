package com.microalert.app.ml.predictor

import android.content.Context
import com.google.gson.Gson
import java.io.InputStream
import kotlin.math.exp

data class ForecastLayer(val w: List<List<Float>>, val b: List<Float>)

data class ForecastModel(
    val lag: Int,
    val ahead: Int,
    val threshold: Float,
    val layers: List<ForecastLayer>
)

/**
 * Red neuronal (MLP 10+3 -> 16 -> 8 -> 1) entrenada para estimar la probabilidad de que
 * el riesgo llegue a nivel ALTO en las próximas [ForecastModel.ahead] ventanas,
 * a partir de las últimas [ForecastModel.lag] probabilidades del clasificador.
 * Se evalúa en el reloj, sin conexión.
 */
class RiskForecaster(private val model: ForecastModel) {

    private val history = ArrayDeque<Float>()

    val lag: Int get() = model.lag

    fun reset() = history.clear()

    /** Registra la probabilidad de la ventana actual y devuelve el pronóstico, o null si faltan datos. */
    fun update(probability: Float): Float? {
        history.addLast(probability)
        while (history.size > model.lag) history.removeFirst()
        if (history.size < model.lag) return null
        return predict(history.toList())
    }

    fun predict(window: List<Float>): Float = forward(features(window))

    internal fun features(h: List<Float>): FloatArray {
        val n = h.size
        val meanX = (n - 1) / 2f
        val meanY = h.average().toFloat()
        var num = 0f
        var den = 0f
        for (i in 0 until n) {
            num += (i - meanX) * (h[i] - meanY)
            den += (i - meanX) * (i - meanX)
        }
        val slope = if (den == 0f) 0f else num / den
        return (h + listOf(slope * 10f, meanY, h.last() - h.first())).toFloatArray()
    }

    private fun forward(input: FloatArray): Float {
        var x = input
        model.layers.forEachIndexed { index, layer ->
            val out = FloatArray(layer.b.size)
            for (j in out.indices) {
                var s = layer.b[j]
                val row = layer.w[j]
                for (i in x.indices) s += row[i] * x[i]
                out[j] = if (index == model.layers.lastIndex) sigmoid(s) else maxOf(0f, s)
            }
            x = out
        }
        return x[0]
    }

    private fun sigmoid(v: Float): Float = (1.0 / (1.0 + exp(-v.toDouble()))).toFloat()

    companion object {
        fun fromStream(stream: InputStream): RiskForecaster =
            RiskForecaster(stream.reader().use { Gson().fromJson(it, ForecastModel::class.java) })

        fun fromAssets(context: Context): RiskForecaster =
            context.assets.open("predictor.json").use { fromStream(it) }
    }
}
