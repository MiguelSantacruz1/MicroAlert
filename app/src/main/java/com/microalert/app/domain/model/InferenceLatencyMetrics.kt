package com.microalert.app.domain.model

/**
 * Métricas de latencia de inferencia y procesamiento para garantizar ejecución < 50ms en Wear OS.
 */
data class InferenceLatencyMetrics(
    val windowAggregationTimeMs: Long = 0L,
    val featureExtractionTimeMs: Long = 0L,
    val treeEvaluationTimeMs: Long = 0L,
    val policyEvaluationTimeMs: Long = 0L
) {
    val totalPipelineLatencyMs: Long
        get() = windowAggregationTimeMs + featureExtractionTimeMs + treeEvaluationTimeMs + policyEvaluationTimeMs

    val isWithinWearOsBudget: Boolean
        get() = totalPipelineLatencyMs < 50L
}
