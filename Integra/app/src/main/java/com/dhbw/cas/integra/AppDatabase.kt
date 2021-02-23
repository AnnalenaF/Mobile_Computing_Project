package com.dhbw.cas.integra

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase.CONFLICT_ABORT
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dhbw.cas.integra.ui.areas.Area
import com.dhbw.cas.integra.ui.areas.AreaDao
import com.dhbw.cas.integra.ui.catalogue.Task
import com.dhbw.cas.integra.ui.catalogue.TaskDao
import java.util.concurrent.Executors


@Database(entities = [Area::class, Task::class], version = 3)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun areaDao(): AreaDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE areas")
                db.execSQL("CREATE TABLE IF NOT EXISTS `areas` (`text` TEXT NOT NULL, `label` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_areas_text` ON `areas` (`text`)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_areas_label` ON `areas` (`label`)")
                db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
                db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'e73c53d6857bceec841faa79e91dd708')")

                db.insert("areas", CONFLICT_ABORT, ContentValues().apply {
                    put("text", "Privat")
                    put("label", R.drawable.shape_area_label_0)
                })
                db.insert("areas", CONFLICT_ABORT, ContentValues().apply {
                    put("text", "Arbeit")
                    put("label", R.drawable.shape_area_label_1)
                })
            }
        }
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE areas")
                db.execSQL("CREATE TABLE IF NOT EXISTS `areas` (`text` TEXT NOT NULL, `label` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_areas_text` ON `areas` (`text`)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_areas_label` ON `areas` (`label`)")
                db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
                db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'e73c53d6857bceec841faa79e91dd708')")

                db.insert("areas", CONFLICT_ABORT, ContentValues().apply {
                    put("text", "Privat")
                    put("label", R.drawable.shape_area_label_0)
                })
                db.insert("areas", CONFLICT_ABORT, ContentValues().apply {
                    put("text", "Arbeit")
                    put("label", R.drawable.shape_area_label_1)
                })

                db.execSQL("CREATE TABLE IF NOT EXISTS `tasks` (`text` TEXT NOT NULL, `priority` INTEGER NOT NULL, `area_id` INTEGER NOT NULL, `expectedDuration` INTEGER, `loggedDuration` INTEGER, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, FOREIGN KEY(`area_id`) REFERENCES `areas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")

            }
        }

        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            if (instance == null){
                instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "integra.db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        // create initial default areas
                        Executors.newSingleThreadScheduledExecutor()
                                .execute {
                                    val areaDao = getDatabase(context).areaDao()
                                    areaDao.insertNow(Area(text = "Privat", label = R.drawable.shape_area_label_0))
                                    areaDao.insertNow(Area(text = "Arbeit", label = R.drawable.shape_area_label_1))
                                }
                    }
                })
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .build()
            }
            return instance!!
        }
    }
}