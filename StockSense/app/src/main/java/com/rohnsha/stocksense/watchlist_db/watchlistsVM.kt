package com.rohnsha.stocksense.watchlist_db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class watchlistsVM(application: Application): AndroidViewModel(application) {

    val readWatchlists: LiveData<List<watchlists>>
    private val watchlistsRepo: watchlistsRepo

    init {
        val watchlistsDAO= watchlistsDB.getWatchlistsDB(application).watchlists_dao()
        watchlistsRepo= watchlistsRepo(watchlistsDAO)
        readWatchlists= watchlistsDAO.readWatchlists()
    }

    fun addWatchlists(watchlists: watchlists){
        viewModelScope.launch(Dispatchers.IO) {
            watchlistsRepo.addWatchlists(watchlists)
        }
    }

}