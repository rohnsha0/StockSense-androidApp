package com.rohnsha.stocksense.technical_api

data class dataclass_technical(
    val ISIN: String,
    val atr: Double,
    val bollingerBandUpper: Double,
    val bollingerBankLoweer: Double,
    val ema100: Double,
    val ema200: Double,
    val ema50: Double,
    val faceValue: Int,
    val industry: String,
    val macd: Double,
    val rsi: Double,
    val sma100: Double,
    val sma200: Double,
    val sma50: Double
)