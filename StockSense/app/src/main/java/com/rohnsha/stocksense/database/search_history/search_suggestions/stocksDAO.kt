package com.rohnsha.stocksense.database.search_history.search_suggestions

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface stocksDAO {
    @Query("SELECT * FROM stocks_search ORDER BY symbol DESC")
    fun readStocks(): LiveData<List<search>>

    @Query("SELECT * FROM stocks_search WHERE company LIKE :serchQuery")
    fun searchDatabase(serchQuery: String): Flow<List<search>>
}