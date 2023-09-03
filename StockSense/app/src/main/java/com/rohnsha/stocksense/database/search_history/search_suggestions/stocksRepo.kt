package com.rohnsha.stocksense.database.search_history.search_suggestions

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class stocksRepo(private val stocksDAO: stocksDAO) {

    var readAllStocks: LiveData<List<search>> = stocksDAO.readStocksNSE()
    var readBSEStocks: LiveData<List<search>> = stocksDAO.readStocksBSE()

    fun searchStocksNSE(searchQuery: String): Flow<List<search>> {
        return stocksDAO.searchDatabaseNSE(searchQuery)
    }

    fun searchStocksBSE(searchQuery: String): Flow<List<search>> {
        return stocksDAO.searchDatabaseBSE(searchQuery)
    }

}