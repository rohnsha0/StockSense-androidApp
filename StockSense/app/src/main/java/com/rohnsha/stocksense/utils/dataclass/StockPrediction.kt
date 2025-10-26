package com.rohnsha.stocksense.utils.dataclass

import com.rohnsha.stocksense.utils.enums.TrendDirection

data class StockPrediction(
    val symbol: String,
    val name: String,
    val currentPrice: String,
    val predictedChange: Float,
    val confidence: Int,
    val trend: TrendDirection,
    val sparklineData: List<Float>
)