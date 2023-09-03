package com.rohnsha.stocksense.database.search_history.search_suggestions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData

class stocksModel(application: Application): AndroidViewModel(application) {

    val readStocks: LiveData<List<search>>
    val readStocksBSE: LiveData<List<search>>
    private val stocksRepo: stocksRepo

    init {
        val stocksDAO= stocksDB.getStockDB(application).stocksDAO()
        stocksRepo=stocksRepo(stocksDAO)
        readStocks= stocksDAO.readStocksNSE()
        readStocksBSE= stocksDAO.readStocksBSE()
    }

    fun searchStocksDBNSE(searchQuery: String): LiveData<List<search>>{
        return stocksRepo.searchStocksNSE(searchQuery).asLiveData()
    }

    fun searchStocksDBBSE(searchQuery: String): LiveData<List<search>>{
        return stocksRepo.searchStocksBSE(searchQuery).asLiveData()
    }

}