package com.rohnsha.stocksense.watchlist_db

import androidx.lifecycle.LiveData

class watchlistsRepo(private val watchlistsDAO: watchlistsDAO) {

    var readWatchlists: LiveData<List<watchlists>> = watchlistsDAO.readWatchlists()

    suspend fun addWatchlists(watchlists: watchlists){
        watchlistsDAO.addWatchlists(watchlists)
    }

}