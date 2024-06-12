package com.rohnsha.stocksense.database.indices_db

import androidx.lifecycle.LiveData

class indicesRepo(private val indices_dao: indicesDAO) {

    var readIndices: LiveData<List<indices>> = indices_dao.readIndices()

    suspend fun addIndices(indices: indices){
        indices_dao.addIndices(indices)
    }

    suspend fun updateIndices(indices: indices){
        indices_dao.updateIndex(indices)
    }

    suspend fun deleteIndices(indices: List<indices>){
        indices_dao.deleteIndice(indices)
    }

    suspend fun searchIndices(indiceSymbol: String): List<indices> {
        return indices_dao.searchIndices(indiceSymbol)
    }

}