package com.rohnsha.stocksense.database.search_history

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history_table")
data class search_history(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val search_history: String?
)
