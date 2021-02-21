package com.dhbw.cas.integra

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dhbw.cas.integra.ui.areas.Area
import com.dhbw.cas.integra.ui.areas.AreaDao

@Database(entities = [Area::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun areaDao(): AreaDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            if (instance == null){
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "integra.db"
                ).build()
            }
            return instance!!
        }
    }
}