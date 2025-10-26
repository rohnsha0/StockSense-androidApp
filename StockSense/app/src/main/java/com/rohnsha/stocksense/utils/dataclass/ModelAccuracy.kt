package com.rohnsha.stocksense.utils.dataclass

data class ModelAccuracy(
    val period: String,
    val accuracy: Float,
    val totalPredictions: Int,
    val correctPredictions: Int
)