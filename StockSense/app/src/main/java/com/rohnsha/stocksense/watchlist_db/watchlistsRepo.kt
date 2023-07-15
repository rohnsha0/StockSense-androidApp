package com.rohnsha.stocksense.watchlist_db

import androidx.lifecycle.LiveData

class watchlistsRepo(private val watchlistsDAO: watchlistsDAO) {

    var readWatchlists: LiveData<List<watchlists>> = watchlistsDAO.readWatchlists()
    var readWatchlistsDesc: LiveData<List<watchlists>> = watchlistsDAO.readWatchlistsDesc()
    var sortLTPAsc: LiveData<List<watchlists>> = watchlistsDAO.sortLTPAsc()
    var sortLTPDesc: LiveData<List<watchlists>> = watchlistsDAO.sortLTPDesc()
    var sortStatusAsc: LiveData<List<watchlists>> = watchlistsDAO.sortStatusAsc()
    var sortStatusDesc: LiveData<List<watchlists>> = watchlistsDAO.sortStatusDesc()

    suspend fun addWatchlists(watchlists: watchlists){
        watchlistsDAO.addWatchlists(watchlists)
    }

    suspend fun deleteWatchlists(watchlists: List<watchlists>){
        watchlistsDAO.deleteWatchlist(watchlists)
    }

    suspend fun searchWatchlists(symbol: String): List<watchlists>{
        return watchlistsDAO.searchWatchlistsDB(symbol)
    }

    suspend fun getDBcount(): Int{
        return watchlistsDAO.getDBcount()
    }

}