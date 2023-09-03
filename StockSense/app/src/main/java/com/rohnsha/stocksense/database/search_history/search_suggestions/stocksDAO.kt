package com.rohnsha.stocksense.database.search_history.search_suggestions

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface stocksDAO {
    @Query("SELECT * FROM stocksBSE ORDER BY company ASC")
    fun readStocksNSE(): LiveData<List<search>>

    @Query("SELECT * FROM stocksNSE ORDER BY company ASC")
    fun readStocksBSE(): LiveData<List<search>>

    @Query("SELECT * FROM stocksNSE WHERE company LIKE :serchQuery OR symbol LIKE :serchQuery")
    fun searchDatabaseNSE(serchQuery: String): Flow<List<search>>

    @Query("SELECT * FROM stocksBSE WHERE company LIKE :serchQuery OR symbol LIKE :serchQuery")
    fun searchDatabaseBSE(serchQuery: String): Flow<List<search>>
}