package com.microalert.app.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InferenceLatencyMetricsTest {

    @Test
    fun `latency metrics within 50ms should pass budget test`() {
        val metrics = InferenceLatencyMetrics(
            windowAggregationTimeMs = 2L,
            featureExtractionTimeMs = 1L,
            treeEvaluationTimeMs = 3L,
            policyEvaluationTimeMs = 1L
        )

        assertEquals(7L, metrics.totalPipelineLatencyMs)
        assertTrue(metrics.isWithinWearOsBudget)
    }
}
