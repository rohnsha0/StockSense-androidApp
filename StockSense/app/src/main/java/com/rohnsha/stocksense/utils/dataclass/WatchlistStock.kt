package com.rohnsha.stocksense.utils.dataclass

// Data Models
data class WatchlistStock(
    val symbol: String,
    val name: String,
    val currentPrice: String,
    val priceChange: Float,
    val changePercent: Float,
    val predictedChange: Float,
    val confidence: Int,
    val volume: String,
    val dayHigh: String,
    val dayLow: String,
    val sparklineData: List<Float>,
    val addedDate: String
)