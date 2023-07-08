package com.rohnsha.stocksense.database.search_history

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface searchDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSearchHistory(searchHistory: search_history)

    @Query("SELECT * FROM search_history_table ORDER BY id DESC")
    fun readSearchHistory(): LiveData<List<search_history>>
}