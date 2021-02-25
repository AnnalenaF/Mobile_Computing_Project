package com.dhbw.cas.integra

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase.CONFLICT_ABORT
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.dhbw.cas.integra.data.*
import java.util.concurrent.Executors


@Database(entities = [Area::class, Task::class, Sprint::class], version = 7)
abstract class AppDatabase : RoomDatabase() {
    abstract fun areaDao(): AreaDao
    abstract fun taskDao(): TaskDao
    abstract fun sprintDao(): SprintDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
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
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `tasks` (`text` TEXT NOT NULL, `priority` INTEGER NOT NULL, `area_id` INTEGER NOT NULL, `expectedDuration` INTEGER, `loggedDuration` INTEGER, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, FOREIGN KEY(`area_id`) REFERENCES `areas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
            }
        }
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE tasks")
                db.execSQL("CREATE TABLE IF NOT EXISTS `tasks` (`title` TEXT NOT NULL, `description` TEXT NOT NULL, `priority` INTEGER NOT NULL, `area_id` INTEGER NOT NULL, `expectedDuration` INTEGER, `loggedDuration` INTEGER, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, FOREIGN KEY(`area_id`) REFERENCES `areas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )")
            }
        }
        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE areas")
                db.execSQL("CREATE TABLE IF NOT EXISTS `areas` (`text` TEXT PRIMARY KEY NOT NULL, `label` INTEGER NOT NULL)")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_areas_label` ON `areas` (`label`)")

                db.insert("areas", CONFLICT_ABORT, ContentValues().apply {
                    put("text", "Privat")
                    put("label", R.drawable.shape_area_label_0)
                })
                db.insert("areas", CONFLICT_ABORT, ContentValues().apply {
                    put("text", "Arbeit")
                    put("label", R.drawable.shape_area_label_1)
                })
                db.execSQL("DROP TABLE tasks")
                db.execSQL("CREATE TABLE IF NOT EXISTS `tasks` (`title` TEXT NOT NULL, `description` TEXT NOT NULL, `priority` INTEGER NOT NULL, `area_text` TEXT NOT NULL, `expectedDuration` INTEGER, `loggedDuration` INTEGER, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, FOREIGN KEY(`area_text`) REFERENCES `areas`(`text`) ON UPDATE NO ACTION ON DELETE CASCADE )")
            }
        }
        private val MIGRATION_5_6 = object : Migration(5, 6){
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE tasks")
                db.execSQL("CREATE TABLE IF NOT EXISTS `tasks` (`title` TEXT NOT NULL, `description` TEXT NOT NULL, `priority` INTEGER NOT NULL, `area_text` TEXT NOT NULL, `expectedDuration` INTEGER NOT NULL, `loggedDuration` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, FOREIGN KEY(`area_text`) REFERENCES `areas`(`text`) ON UPDATE NO ACTION ON DELETE CASCADE )")
            }
        }
        private val MIGRATION_6_7 = object : Migration(6, 7){
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE areas")
                db.execSQL("CREATE TABLE IF NOT EXISTS `areas` (`text` TEXT NOT NULL, `label` INTEGER NOT NULL, `sprintId` INTEGER, `totalCapacity` INTEGER, `remainingCapacity` INTEGER, PRIMARY KEY(`text`), FOREIGN KEY(`sprintId`) REFERENCES `sprints`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_areas_label` ON `areas` (`label`)")

                db.insert("areas", CONFLICT_ABORT, ContentValues().apply {
                    put("text", "Privat")
                    put("label", R.drawable.shape_area_label_0)
                })
                db.insert("areas", CONFLICT_ABORT, ContentValues().apply {
                    put("text", "Arbeit")
                    put("label", R.drawable.shape_area_label_1)
                })

                db.execSQL("DROP TABLE tasks")
                db.execSQL("CREATE TABLE IF NOT EXISTS `tasks` (`title` TEXT NOT NULL, `description` TEXT NOT NULL, `priority` INTEGER NOT NULL, `area_text` TEXT NOT NULL, `expectedDuration` INTEGER NOT NULL, `loggedDuration` INTEGER NOT NULL, `sprintId` INTEGER, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, FOREIGN KEY(`area_text`) REFERENCES `areas`(`text`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`sprintId`) REFERENCES `sprints`(`id`) ON UPDATE NO ACTION ON DELETE NO ACTION )")

                db.execSQL("CREATE TABLE IF NOT EXISTS `sprints` (`isActive` INTEGER NOT NULL, `startDate` INTEGER NOT NULL, `endDate` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
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
                    MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5,
                    MIGRATION_5_6, MIGRATION_6_7
                )
                .build()
            }
            return instance!!
        }
    }
}