package com.rohnsha.stocksense.watchlist_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface watchlistsDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addWatchlists(watchlists: watchlists)

    @Query("SELECT * FROM watchlists_table ORDER BY symbol ASC")
    fun readWatchlists(): LiveData<List<watchlists>>

}