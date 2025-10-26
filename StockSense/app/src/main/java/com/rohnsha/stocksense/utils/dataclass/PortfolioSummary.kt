package com.rohnsha.stocksense.utils.dataclass

data class PortfolioSummary(
    val totalValue: String,
    val totalGainLoss: Float,
    val totalGainLossAmount: String,
    val stocksCount: Int,
    val bestPerformer: String,
    val worstPerformer: String
)