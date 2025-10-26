package com.rohnsha.stocksense.utils.dataclass

data class SectorPerformance(
    val sectorName: String,
    val changePercent: Float,
    val marketCap: String,
    val topStocks: List<String>,
    val volume: String
)