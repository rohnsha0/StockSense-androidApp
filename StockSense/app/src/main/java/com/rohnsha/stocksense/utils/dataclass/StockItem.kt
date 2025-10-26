package com.rohnsha.stocksense.utils.dataclass

// Data Models
data class StockItem(
    val symbol: String,
    val name: String,
    val sector: String,
    val currentPrice: String,
    val priceChange: Float,
    val predictedChange: Float,
    val confidence: Int,
    val volume: String,
    val sparklineData: List<Float>,
    val isInWatchlist: Boolean = false
)