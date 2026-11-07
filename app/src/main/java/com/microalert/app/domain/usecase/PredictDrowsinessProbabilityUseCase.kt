package com.microalert.app.domain.usecase

import com.microalert.app.data.repository.DrowsinessModelRepository
import com.microalert.app.domain.model.FeatureVector

class PredictDrowsinessProbabilityUseCase(
    private val modelRepository: DrowsinessModelRepository
) {
    operator fun invoke(featureVector: FeatureVector): Float {
        return modelRepository.predictDrowsinessProbability(featureVector)
    }
}
