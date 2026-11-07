package com.microalert.app.domain.model

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
