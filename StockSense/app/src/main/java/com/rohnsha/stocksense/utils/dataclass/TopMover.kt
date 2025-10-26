package com.rohnsha.stocksense.utils.dataclass

data class TopMover(
    val symbol: String,
    val name: String,
    val changePercent: Float
)