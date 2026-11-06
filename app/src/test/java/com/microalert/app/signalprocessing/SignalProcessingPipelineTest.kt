package com.microalert.app.signalprocessing

import com.microalert.app.sensors.model.SensorSample
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SignalProcessingPipelineTest {

    @Test
    fun `pipeline should emit processed window when step threshold is reached`() = runTest {
        val pipeline = SignalProcessingPipeline(windowDurationMs = 60_000L, windowStepMs = 30_000L)

        var emittedWindow = false
        val job = launch {
            val window = pipeline.windowStream.first()
            assertNotNull(window)
            assertEquals(2, window.sampleCount)
            emittedWindow = true
        }

        pipeline.processSample(SensorSample(timestampMs = 1000L, heartRateBpm = 72f))
        pipeline.processSample(SensorSample(timestampMs = 31_000L, heartRateBpm = 74f))

        job.join()
        assertEquals(true, emittedWindow)
    }
}
