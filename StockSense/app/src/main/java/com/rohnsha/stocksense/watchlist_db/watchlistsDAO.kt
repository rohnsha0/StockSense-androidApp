package com.rohnsha.stocksense.watchlist_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface watchlistsDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addWatchlists(watchlists: watchlists)

    @Delete
    suspend fun deleteWatchlist(watchlists: List<watchlists>)

    @Query("SELECT * FROM watchlists_table ORDER BY symbol ASC")
    fun readWatchlists(): LiveData<List<watchlists>>

    @Query("SELECT * FROM watchlists_table WHERE symbol LIKE :inpSymbol")
    fun searchWatchlistsDB(inpSymbol: String): List<watchlists>

}