package com.rohnsha.stocksense.watchlist_db

import androidx.lifecycle.LiveData

class watchlistsRepo(private val watchlistsDAO: watchlistsDAO) {

    var readWatchlists: LiveData<List<watchlists>> = watchlistsDAO.readWatchlists()

    suspend fun addWatchlists(watchlists: watchlists){
        watchlistsDAO.addWatchlists(watchlists)
    }

    suspend fun deleteWatchlists(watchlists: List<watchlists>){
        watchlistsDAO.deleteWatchlist(watchlists)
    }

    suspend fun searchWatchlists(symbol: String): List<watchlists>{
        return watchlistsDAO.searchWatchlistsDB(symbol)
    }

}