package com.rohnsha.stocksense.api.stock_infoAPI

data class dataclass_stocksInfo(
    val W52High: Double,
    val W52Low: Double,
    val company: String,
    val d1: String,
    val d2: String,
    val d3: String,
    val d4: String,
    val d5: String,
    val d6: String,
    val dayHigh: Double,
    val dayLow: Double,
    val face_value: Int,
    val indusry: String,
    val isin: String,
    val t1: Double,
    val t2: Double,
    val t3: Double,
    val t4: Double,
    val t5: Double,
    val t6: Double,
    val dayOpen: Double
)