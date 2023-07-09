package com.rohnsha.stocksense.watchlist_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [watchlists::class], version = 1, exportSchema = false)
abstract class watchlistsDB: RoomDatabase() {

    abstract fun watchlists_dao(): watchlistsDAO

    companion object{
        @Volatile
        private var INSTANCE: watchlistsDB? = null

        fun getWatchlistsDB(context: Context): watchlistsDB{
            val getWLtemp = INSTANCE
            if (getWLtemp!=null){
                return getWLtemp
            }
            synchronized(this){
                val instanceWL= Room.databaseBuilder(
                    context.applicationContext,
                    watchlistsDB::class.java,
                    "watchlists_db"
                ).build()
                INSTANCE= instanceWL
                return instanceWL
            }
        }
    }

}