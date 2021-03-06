package com.dhbw.cas.integra

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dhbw.cas.integra.data.*
import java.util.concurrent.Executors


@Database(entities = [Area::class, Task::class, Sprint::class], version = 12)
abstract class AppDatabase : RoomDatabase() {
    abstract fun areaDao(): AreaDao
    abstract fun taskDao(): TaskDao
    abstract fun sprintDao(): SprintDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        val MIGRATION_11_12 = object : Migration(11, 12){
            override fun migrate(db: SupportSQLiteDatabase) {
                // drop all tables
                db.execSQL("DROP TABLE IF EXISTS `areas`")
                db.execSQL("DROP TABLE IF EXISTS `tasks`")
                db.execSQL("DROP TABLE IF EXISTS `sprints`")

                // create all tables
                db.execSQL("CREATE TABLE IF NOT EXISTS `areas` (`text` TEXT NOT NULL, `label` INTEGER NOT NULL, `totalCapacity` INTEGER, `remainingCapacity` INTEGER, PRIMARY KEY(`text`))")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_areas_label` ON `areas` (`label`)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `tasks` (`title` TEXT NOT NULL, `description` TEXT NOT NULL, `priority` INTEGER NOT NULL, `area_text` TEXT NOT NULL, `expectedDuration` INTEGER NOT NULL, `loggedDuration` INTEGER NOT NULL, `sprintId` INTEGER, `state` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, FOREIGN KEY(`area_text`) REFERENCES `areas`(`text`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`sprintId`) REFERENCES `sprints`(`id`) ON UPDATE NO ACTION ON DELETE SET DEFAULT )")
                db.execSQL("CREATE TABLE IF NOT EXISTS `sprints` (`isActive` INTEGER NOT NULL, `startDate` INTEGER NOT NULL, `endDate` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
                db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '81cf289ea49effad5334d389763cf6c2')")

                db.insert("areas", SQLiteDatabase.CONFLICT_ABORT, ContentValues().apply {
                    put("text", "Privat")
                    put("label", R.drawable.shape_area_label_0)
                })
                db.insert("areas", SQLiteDatabase.CONFLICT_ABORT, ContentValues().apply {
                    put("text", "Arbeit")
                    put("label", R.drawable.shape_area_label_1)
                })
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
                                areaDao.insertNow(
                                    Area(
                                        text = "Privat",
                                        label = R.drawable.shape_area_label_0
                                    )
                                )
                                areaDao.insertNow(
                                    Area(
                                        text = "Arbeit",
                                        label = R.drawable.shape_area_label_1
                                    )
                                )
                            }
                    }
                })
                .addMigrations(
                    MIGRATION_11_12
                )
                .allowMainThreadQueries()
                .build()
            }
            return instance!!
        }
    }
}