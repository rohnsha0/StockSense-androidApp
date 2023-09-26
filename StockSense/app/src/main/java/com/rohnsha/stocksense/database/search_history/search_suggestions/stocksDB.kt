package com.rohnsha.stocksense.database.search_history.search_suggestions

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [search::class, stocksNSE::class], version = 1, exportSchema = false)
abstract class stocksDB: RoomDatabase() {

    abstract fun stocksDAO(): stocksDAO

    companion object{
        @Volatile
        private var INSTANCEstocks: stocksDB? = null

        fun getStockDB(context: Context): stocksDB{
            val getStocksTemp= INSTANCEstocks

            if (getStocksTemp!=null){
                return getStocksTemp
            }

            synchronized(this){
                val instanceStocks= Room.databaseBuilder(
                    context.applicationContext,
                    stocksDB::class.java,
                    "stocksDatabase"
                ).createFromAsset("database/stocksDatabase.db").build()
                INSTANCEstocks= instanceStocks
                return instanceStocks
            }
        }
    }

}