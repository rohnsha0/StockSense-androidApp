package com.rohnsha.stocksense.watchlist_db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("watchlists_table")
data class watchlists(
    @PrimaryKey(autoGenerate = false)
    val symbol: String,
    val company: String,
    val ltp: Double,
    val status: String
)
