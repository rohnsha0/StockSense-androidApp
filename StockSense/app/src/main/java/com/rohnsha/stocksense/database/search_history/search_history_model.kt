package com.rohnsha.stocksense.database.search_history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class search_history_model(application: Application): AndroidViewModel(application) {

    val readSearchHistory:LiveData<List<search_history>>
    private val searchRepo: search_history_repo

    init {
        val searchDAO= search_history_DB.getSearchDB(application).search_history_DAO()
        searchRepo= search_history_repo(searchDAO)
        readSearchHistory= searchRepo.readAllHistory
    }

    fun addHistory(searchHistory: search_history){
        viewModelScope.launch(Dispatchers.IO){
            searchRepo.addHistory(searchHistory)
        }
    }

    suspend fun countDBquery(): Int{
        return withContext(Dispatchers.IO){
            searchRepo.countDB()
        }
    }

}