package com.microalert.app.signalprocessing

import com.microalert.app.signalprocessing.model.ReactionTestResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ReactionTestManagerTest {

    @Test
    fun `successful reaction result should be returned when valid`() {
        val manager = ReactionTestManager(maxValidityDurationMs = 15 * 60 * 1000L)
        val result = ReactionTestResult(
            timestampMs = System.currentTimeMillis(),
            latencyMs = 320L,
            isSuccessful = true,
            isTimedOut = false
        )

        manager.recordResult(result)
        val reactionMs = manager.getValidReactionTimeMs()

        assertEquals(320f, reactionMs)
    }

    @Test
    fun `timed out or unsuccessful reaction test should return null`() {
        val manager = ReactionTestManager()
        val result = ReactionTestResult(
            timestampMs = System.currentTimeMillis(),
            latencyMs = null,
            isSuccessful = false,
            isTimedOut = true
        )

        manager.recordResult(result)
        assertNull(manager.getValidReactionTimeMs())
    }

    @Test
    fun `expired reaction result older than max validity should return null`() {
        val manager = ReactionTestManager(maxValidityDurationMs = 60_000L)
        val oldTimestamp = System.currentTimeMillis() - 120_000L
        val result = ReactionTestResult(
            timestampMs = oldTimestamp,
            latencyMs = 280L,
            isSuccessful = true,
            isTimedOut = false
        )

        manager.recordResult(result)
        assertNull(manager.getValidReactionTimeMs())
    }

    @Test
    fun `clearing manager should discard previous reaction results`() {
        val manager = ReactionTestManager()
        val result = ReactionTestResult(
            timestampMs = System.currentTimeMillis(),
            latencyMs = 350L,
            isSuccessful = true,
            isTimedOut = false
        )

        manager.recordResult(result)
        manager.clear()
        assertNull(manager.getValidReactionTimeMs())
    }
}
