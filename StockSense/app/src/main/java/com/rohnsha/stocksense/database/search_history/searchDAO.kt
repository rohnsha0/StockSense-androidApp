package com.rohnsha.stocksense.database.search_history

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface searchDAO {

    @Delete
    suspend fun deleteSearch(search: search_history)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSearchHistory(searchHistory: search_history)

    @Query("SELECT * FROM search_history_table ORDER BY id DESC")
    fun readSearchHistory(): LiveData<List<search_history>>

    @Query("SELECT COUNT(id) FROM search_history_table")
    fun getDBcount(): Int
}