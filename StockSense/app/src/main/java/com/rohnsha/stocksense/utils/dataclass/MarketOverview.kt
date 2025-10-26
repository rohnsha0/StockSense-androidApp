package com.rohnsha.stocksense.utils.dataclass

// Data Models
data class MarketOverview(
    val id: String,
    val name: String,  // e.g., "NIFTY 50", "SENSEX", "BANKNIFTY"
    val indexValue: String,
    val changePercent: Float,
    val volume: String,
    val timestamp: String
)