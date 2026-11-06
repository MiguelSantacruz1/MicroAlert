package com.microalert.app.domain.usecase

import com.microalert.app.data.repository.DrowsinessModelRepository
import com.microalert.app.domain.model.FeatureVector

/**
 * Caso de uso para calcular la probabilidad de somnolencia a partir del vector de características.
 */
class PredictDrowsinessProbabilityUseCase(
    private val modelRepository: DrowsinessModelRepository
) {
    operator fun invoke(featureVector: FeatureVector): Float {
        return modelRepository.predictDrowsinessProbability(featureVector)
    }
}
