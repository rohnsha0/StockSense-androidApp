package com.rohnsha.stocksense.pred_glance_db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pred_glance_table")
data class pred_glance(
    @PrimaryKey(autoGenerate = false)
    val symbol: String,
    val company: String,
    val ltp: Double,
    val prediction: Double,
    val trend: String,
    val remarks: String
)
