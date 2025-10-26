package com.rohnsha.stocksense.utils.dataclass

// Data Models
data class MarketSentiment(
    val sentiment: String,
    val score: Float, // -1.0 to 1.0
    val description: String,
    val indicators: List<SentimentIndicator>
)