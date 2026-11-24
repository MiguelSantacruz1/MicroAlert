package com.microalert.app.presentation.reaction

import androidx.lifecycle.ViewModel
import com.microalert.app.presentation.theme.tr
import androidx.lifecycle.viewModelScope
import com.microalert.app.signalprocessing.ReactionTestManager
import com.microalert.app.signalprocessing.model.ReactionTestResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class ReactionTestViewModel(
    private val reactionTestManager: ReactionTestManager? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReactionTestUiState())
    val uiState: StateFlow<ReactionTestUiState> = _uiState.asStateFlow()

    private var stimulusStartTimeMs: Long = 0L
    private var testJob: Job? = null

    fun startTestSequence() {
        testJob?.cancel()
        _uiState.update { it.copy(phase = ReactionTestPhase.WAITING_FOR_STIMULUS, message = tr("Espera la señal...", "Wait for the signal...")) }

        testJob = viewModelScope.launch {
            val randomDelay = Random.nextLong(2000L, 5000L)
            delay(randomDelay)
            stimulusStartTimeMs = System.currentTimeMillis()
            _uiState.update { it.copy(phase = ReactionTestPhase.STIMULUS_ACTIVE, message = tr("¡AHORA!", "NOW!")) }

            delay(5000L)
            if (_uiState.value.phase == ReactionTestPhase.STIMULUS_ACTIVE) {
                reactionTestManager?.recordResult(
                    ReactionTestResult(latencyMs = null, isSuccessful = false, isTimedOut = true)
                )
                _uiState.update {
                    it.copy(
                        phase = ReactionTestPhase.COMPLETED,
                        lastLatencyMs = null,
                        message = tr("Sin respuesta a tiempo.", "No response in time.")
                    )
                }
            }
        }
    }

    fun onUserReaction() {
        if (_uiState.value.phase == ReactionTestPhase.STIMULUS_ACTIVE) {
            testJob?.cancel()
            val latency = System.currentTimeMillis() - stimulusStartTimeMs
            reactionTestManager?.recordResult(
                ReactionTestResult(latencyMs = latency, isSuccessful = true, isTimedOut = false)
            )
            _uiState.update {
                it.copy(
                    phase = ReactionTestPhase.COMPLETED,
                    lastLatencyMs = latency,
                    message = "${latency} ms"
                )
            }
        } else if (_uiState.value.phase == ReactionTestPhase.WAITING_FOR_STIMULUS) {
            testJob?.cancel()
            _uiState.update {
                it.copy(
                    phase = ReactionTestPhase.INSTRUCTIONS,
                    message = tr("Te adelantaste. Inténtalo de nuevo.", "Too early. Try again.")
                )
            }
        }
    }

    fun reset() {
        testJob?.cancel()
        _uiState.update { ReactionTestUiState() }
    }
}
