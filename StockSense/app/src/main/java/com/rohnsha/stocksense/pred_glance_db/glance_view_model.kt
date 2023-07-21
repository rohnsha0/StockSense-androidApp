package com.rohnsha.stocksense.pred_glance_db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class glance_view_model(application: Application): AndroidViewModel(application) {

    private val glanceRepo: glance_repo

    init {
        val glanceDAO= glance_db.getGlanceDB(application).glanceDAO()
        glanceRepo= glance_repo(glanceDAO)
    }

    fun addGlance(predGlance: pred_glance){
        viewModelScope.launch(Dispatchers.IO){
            glanceRepo.addGlance(predGlance)
        }
    }

    suspend fun queryGlance(): pred_glance{
        return withContext(Dispatchers.IO){
            glanceRepo.queryGlanceDB()
        }
    }

    suspend fun getDBcount(): Int{
        return withContext(Dispatchers.IO){
            glanceRepo.getDbCount()
        }
    }

    suspend fun searchDBGlance(symbol: String): List<pred_glance>{
        return withContext(Dispatchers.IO){
            glanceRepo.checkDBGlance(symbol)
        }
    }

    suspend fun deleteGlance(predGlance: List<pred_glance>){
        viewModelScope.launch(Dispatchers.IO){
            glanceRepo.deleteGlance(predGlance)
        }
    }

}