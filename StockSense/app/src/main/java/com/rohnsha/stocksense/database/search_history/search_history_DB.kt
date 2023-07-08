package com.rohnsha.stocksense.database.search_history

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [search_history::class], version = 1, exportSchema = false)
abstract class search_history_DB: RoomDatabase() {

    abstract fun search_history_DAO(): searchDAO

    companion object{
        @Volatile
        private var INSTANCE: search_history_DB? = null

        fun getSearchDB(context: Context): search_history_DB {
            val getSearchTemp = INSTANCE
            if (getSearchTemp != null){
                return getSearchTemp
            }
            synchronized(this){
                val instanceSearch= Room.databaseBuilder(
                    context.applicationContext,
                    search_history_DB::class.java,
                    "search_history_DB"
                ).build()
                INSTANCE =instanceSearch
                return instanceSearch
            }
        }
    }

}