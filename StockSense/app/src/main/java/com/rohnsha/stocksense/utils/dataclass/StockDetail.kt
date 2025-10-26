package com.rohnsha.stocksense.utils.dataclass

// Data Models
data class StockDetail(
    val symbol: String,
    val name: String,
    val currentPrice: String,
    val changePercent: Float,
    val changeAmount: String,
    val dayHigh: String,
    val dayLow: String,
    val open: String,
    val previousClose: String,
    val volume: String,
    val marketCap: String,
    val peRatio: String,
    val week52High: String,
    val week52Low: String
)