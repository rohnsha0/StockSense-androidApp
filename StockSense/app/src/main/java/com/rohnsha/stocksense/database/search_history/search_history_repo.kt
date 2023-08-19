package com.rohnsha.stocksense.database.search_history

import androidx.lifecycle.LiveData

class search_history_repo(private val searchDAO: searchDAO) {

    var readAllHistory: LiveData<List<search_history>> = searchDAO.readSearchHistory()

    suspend fun addHistory(searchHistory: search_history){
        searchDAO.addSearchHistory(searchHistory)
    }

    suspend fun dltSearch(search: search_history){
        searchDAO.deleteSearch(search)
    }

    suspend fun countDB(): Int{
        return searchDAO.getDBcount()
    }
}