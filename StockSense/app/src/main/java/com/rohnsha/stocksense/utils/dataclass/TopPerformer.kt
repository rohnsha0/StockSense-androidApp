package com.rohnsha.stocksense.utils.dataclass

data class TopPerformer(
    val symbol: String,
    val name: String,
    val predictedChange: Float,
    val confidence: Int,
    val timeframe: String
)