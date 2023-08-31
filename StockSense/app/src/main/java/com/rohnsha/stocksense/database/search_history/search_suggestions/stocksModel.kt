package com.rohnsha.stocksense.database.search_history.search_suggestions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.asLiveData

class stocksModel(application: Application): AndroidViewModel(application) {

    val readStocks: LiveData<List<search>>
    private val stocksRepo: stocksRepo

    init {
        val stocksDAO= stocksDB.getStockDB(application).stocksDAO()
        stocksRepo=stocksRepo(stocksDAO)
        readStocks= stocksDAO.readStocks()
    }

    fun searchStocksDB(searchQuery: String): LiveData<List<search>>{
        return stocksRepo.searchStocks(searchQuery).asLiveData()
    }

}