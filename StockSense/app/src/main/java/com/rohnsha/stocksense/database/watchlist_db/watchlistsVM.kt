package com.rohnsha.stocksense.database.watchlist_db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class watchlistsVM(application: Application): AndroidViewModel(application) {

    val readWatchlists: LiveData<List<watchlists>>
    val readWatchlistsDesc: LiveData<List<watchlists>>
    var sortLTPAsc: LiveData<List<watchlists>>
    var sortLTPDesc: LiveData<List<watchlists>>
    var sortStatusAsc: LiveData<List<watchlists>>
    var sortStatusDesc: LiveData<List<watchlists>>
    private val watchlistsRepo: watchlistsRepo

    init {
        val watchlistsDAO= watchlistsDB.getWatchlistsDB(application).watchlists_dao()
        watchlistsRepo= watchlistsRepo(watchlistsDAO)
        readWatchlists= watchlistsDAO.readWatchlists()
        readWatchlistsDesc= watchlistsDAO.readWatchlistsDesc()
        sortLTPAsc= watchlistsDAO.sortLTPAsc()
        sortLTPDesc= watchlistsDAO.sortLTPDesc()
        sortStatusAsc= watchlistsDAO.sortStatusAsc()
        sortStatusDesc= watchlistsDAO.sortStatusDesc()
    }

    fun addWatchlists(watchlists: watchlists){
        viewModelScope.launch(Dispatchers.IO) {
            watchlistsRepo.addWatchlists(watchlists)
        }
    }

    fun updateWatchlists(watchlists: watchlists){
        viewModelScope.launch(Dispatchers.IO){
            watchlistsRepo.updateWatchlists(watchlists)
        }
    }

    fun deleteUser(watchlists: List<watchlists>){
        viewModelScope.launch(Dispatchers.IO){
            watchlistsRepo.deleteWatchlists(watchlists)
        }
    }

    suspend fun searchWatchlistsDB(symbol: String): List<watchlists>{
        return withContext(Dispatchers.IO){
            watchlistsRepo.searchWatchlists(symbol = symbol)
        }
    }

    suspend fun getDBcountWL(): Int{
        return withContext(Dispatchers.IO){
            watchlistsRepo.getDBcount()
        }
    }
}