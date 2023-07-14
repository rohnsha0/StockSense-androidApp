package com.rohnsha.stocksense.indices_db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class indicesViewModel(application: Application): AndroidViewModel(application) {

    val readIndices: LiveData<List<indices>>
    private val indices_repo: indicesRepo

    init {
        val indices_dao= indicesDB.getIndicesDB(application).indices_dao()
        indices_repo= indicesRepo(indices_dao)
        readIndices= indices_dao.readIndices()
    }

    suspend fun addIndices(indices: indices){
        viewModelScope.launch(Dispatchers.IO) {
            indices_repo.addIndices(indices)
        }
    }

    suspend fun aupdateIndices(indices: indices){
        viewModelScope.launch(Dispatchers.IO){
            indices_repo.updateIndices(indices)
        }
    }

    fun deleteIndices(indices: List<indices>){
        viewModelScope.launch(Dispatchers.IO){
            indices_repo.deleteIndices(indices)
        }
    }

    suspend fun searchIndices(inpSymbol: String): List<indices>{
        return withContext(Dispatchers.IO){
            indices_repo.searchIndices(inpSymbol)
        }
    }

}