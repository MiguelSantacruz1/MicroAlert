package com.microalert.app.domain.usecase

import com.microalert.app.domain.classifier.RiskPolicyManager
import com.microalert.app.domain.model.FeatureVector
import com.microalert.app.domain.model.RiskAssessmentResult
import com.microalert.app.domain.model.WindowDataQuality

class EvaluateRiskUseCase(
    private val predictDrowsinessProbabilityUseCase: PredictDrowsinessProbabilityUseCase,
    private val riskPolicyManager: RiskPolicyManager = RiskPolicyManager()
) {
    operator fun invoke(
        featureVector: FeatureVector,
        timestampMs: Long = System.currentTimeMillis()
    ): RiskAssessmentResult {
        val probability = predictDrowsinessProbabilityUseCase(featureVector)
        val dataQuality = WindowDataQuality.fromFeatureVector(featureVector)
        return riskPolicyManager.evaluateProbability(probability, dataQuality, timestampMs)
    }

    fun reset() {
        riskPolicyManager.reset()
    }
}
