package com.dhbw.cas.integra

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dhbw.cas.integra.ui.areas.Area
import com.dhbw.cas.integra.ui.areas.AreaDao
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors


@Database(entities = [Area::class], version = 1)
abstract class AppDatabase() : RoomDatabase() {
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
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        // create initial default areas
                        Executors.newSingleThreadScheduledExecutor()
                            .execute( {
                                val areaDao = getDatabase(context).areaDao()
                                areaDao.insertNow(Area(text="Privat", label = R.drawable.shape_area_label_0))
                                areaDao.insertNow(Area(text="Arbeit", label = R.drawable.shape_area_label_1))
                            })
                    }
                })
                .build()
            }
            return instance!!
        }
    }
}