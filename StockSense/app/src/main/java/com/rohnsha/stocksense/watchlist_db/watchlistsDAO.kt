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

    @Query("SELECT COUNT(symbol) FROM watchlists_table")
    fun getDBcount(): Int

    @Delete
    suspend fun deleteWatchlist(watchlists: List<watchlists>)

    @Query("SELECT * FROM watchlists_table ORDER BY symbol ASC")
    fun readWatchlists(): LiveData<List<watchlists>>

    @Query("SELECT * FROM watchlists_table ORDER BY symbol DESC")
    fun readWatchlistsDesc(): LiveData<List<watchlists>>

    @Query("SELECT * FROM watchlists_table ORDER BY ltp ASC")
    fun sortLTPAsc(): LiveData<List<watchlists>>

    @Query("SELECT * FROM watchlists_table ORDER BY ltp DESC")
    fun sortLTPDesc(): LiveData<List<watchlists>>

    @Query("SELECT * FROM watchlists_table ORDER BY status ASC")
    fun sortStatusAsc(): LiveData<List<watchlists>>

    @Query("SELECT * FROM watchlists_table ORDER BY status DESC")
    fun sortStatusDesc(): LiveData<List<watchlists>>

    @Query("SELECT * FROM watchlists_table WHERE symbol LIKE :inpSymbol")
    fun searchWatchlistsDB(inpSymbol: String): List<watchlists>

}