package com.rohnsha.stocksense.utils.dataclass

data class StockSuggestion(
    val symbol: String,
    val name: String,
    val price: String,
    val change: Float,
    val confidence: Int
)