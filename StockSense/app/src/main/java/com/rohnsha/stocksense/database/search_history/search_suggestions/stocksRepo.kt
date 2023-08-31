package com.rohnsha.stocksense.database.search_history.search_suggestions

import androidx.lifecycle.LiveData
import kotlinx.coroutines.flow.Flow

class stocksRepo(private val stocksDAO: stocksDAO) {

    var readAllStocks: LiveData<List<search>> = stocksDAO.readStocks()

    fun searchStocks(searchQuery: String): Flow<List<search>> {
        return stocksDAO.searchDatabase(searchQuery)
    }

}