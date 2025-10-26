package com.rohnsha.stocksense.utils.dataclass

data class PredictionDetail(
    val nextDayPrediction: String,
    val predictedChange: Float,
    val confidence: Int,
    val targetPrice: String,
    val predictionDate: String,
    val modelAccuracy: Int
)