package com.rohnsha.stocksense.database.pred_glance_db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface glance_dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGlance(predGlance: pred_glance)

    @Query("SELECT * FROM pred_glance_table LIMIT 1")
    suspend fun queryDBglance(): pred_glance

    @Query("SELECT COUNT(symbol) FROM pred_glance_table")
    fun getaDBcount(): Int

    @Delete
    suspend fun deleteGlance(predGlance: List<pred_glance>)

    @Query("SELECT * FROM pred_glance_table WHERE symbol LIKE :symbol")
    fun searchGlanceDB(symbol: String): List<pred_glance>
}