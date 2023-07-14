package com.rohnsha.stocksense.indices_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [indices::class], version = 1, exportSchema = false)
abstract class indicesDB: RoomDatabase() {

    abstract fun indices_dao(): indicesDAO

    companion object{
        @Volatile
        private var INSTANCE: indicesDB? = null

        fun getIndicesDB(context: Context): indicesDB {
            val getIndicestemp = INSTANCE
            if (getIndicestemp!=null){
                return getIndicestemp
            }
            synchronized(this){
                val instanceIndice= Room.databaseBuilder(
                    context.applicationContext,
                    indicesDB::class.java,
                    "indices_db"
                ).build()
                INSTANCE= instanceIndice
                return instanceIndice
            }
        }
    }

}