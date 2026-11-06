package com.microalert.app.domain.usecase

import com.microalert.app.domain.model.FeatureVector
import com.microalert.app.signalprocessing.FeatureVectorExtractor
import com.microalert.app.signalprocessing.ReactionTestManager
import com.microalert.app.signalprocessing.model.ProcessedWindowData

/**
 * Caso de uso para consolidar el Vector de 8 Características listo para la inferencia de ML.
 */
class ExtractFeatureVectorUseCase(
    private val extractor: FeatureVectorExtractor,
    private val reactionTestManager: ReactionTestManager
) {
    operator fun invoke(
        windowData: ProcessedWindowData,
        sessionDurationSeconds: Float
    ): FeatureVector {
        val validReactionMs = reactionTestManager.getValidReactionTimeMs()
        return extractor.extractFeatureVector(
            processedWindow = windowData,
            reactionTimeMs = validReactionMs,
            sessionDurationSeconds = sessionDurationSeconds
        )
    }
}
