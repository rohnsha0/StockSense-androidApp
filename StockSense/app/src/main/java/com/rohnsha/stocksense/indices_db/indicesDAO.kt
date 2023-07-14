package com.rohnsha.stocksense.indices_db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface indicesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addIndices(indices: indices)

    @Query("SELECT * FROM indices_table ORDER BY symbol ASC")
    fun readIndices(): LiveData<List<indices>>

    @Delete
    suspend fun deleteIndice(indices: List<indices>)

    @Update
    suspend fun updateIndex(indices: indices)

    @Query("SELECT * FROM indices_table WHERE symbol LIKE :indiceSymbol")
    fun searchIndices(indiceSymbol: String): List<indices>

}