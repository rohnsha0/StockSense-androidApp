package com.rohnsha.stocksense.database.indices_db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "indices_table")
data class indices(
    @PrimaryKey(autoGenerate = false)
    val symbol: String,
    val company: String,
    val ltp: Double,
    val status: String
)