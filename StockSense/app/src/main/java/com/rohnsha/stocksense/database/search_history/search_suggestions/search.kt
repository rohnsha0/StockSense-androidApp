package com.rohnsha.stocksense.database.search_history.search_suggestions

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stocks_search")
data class search(
    @PrimaryKey(autoGenerate = false)
    val symbol: String,
    val company: String
)
