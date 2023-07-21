package com.rohnsha.stocksense.pred_glance_db

class glance_repo(private val glanceDao: glance_dao) {

    suspend fun addGlance(predGlance: pred_glance){
        glanceDao.addGlance(predGlance)
    }

    suspend fun queryGlanceDB(): pred_glance{
        return glanceDao.queryDBglance()
    }

    suspend fun getDbCount(): Int{
        return glanceDao.getaDBcount()
    }

    suspend fun checkDBGlance(symbol: String): List<pred_glance>{
        return glanceDao.searchGlanceDB(symbol)
    }

    suspend fun deleteGlance(predGlance: List<pred_glance>){
        glanceDao.deleteGlance(predGlance)
    }
}