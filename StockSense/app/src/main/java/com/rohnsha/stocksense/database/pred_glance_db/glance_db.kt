package com.rohnsha.stocksense.database.pred_glance_db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [pred_glance::class],
    version = 1,
    exportSchema = false
) abstract class glance_db: RoomDatabase() {

    abstract fun glanceDAO(): glance_dao

    companion object{
        private var INSTANCE: glance_db?=null

        fun getGlanceDB(context: Context): glance_db {
            val getGlanceTemp = INSTANCE
            if (getGlanceTemp!=null){
                return getGlanceTemp
            }
            synchronized(this){
                val instanceGlance= Room.databaseBuilder(
                    context.applicationContext,
                    glance_db::class.java,
                    "glance_db"
                ).build()
                INSTANCE =instanceGlance
                return instanceGlance
            }
        }
    }
}