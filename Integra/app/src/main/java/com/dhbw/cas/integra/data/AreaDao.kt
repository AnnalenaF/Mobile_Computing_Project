package com.dhbw.cas.integra.data

import android.database.sqlite.SQLiteConstraintException
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AreaDao {
    @Query("SELECT * FROM areas ORDER BY text ASC")
    fun getAreas(): LiveData<List<Area>>

    @Query("SELECT text FROM areas")
    fun getAreaTexts(): LiveData<List<String>>

    @Query("SELECT label FROM areas WHERE text = :text")
    fun getLabelByText(text: String): Int

    @Transaction
    @Query("SELECT * FROM areas ORDER BY text ASC")
    fun getAreasWithTasks(): LiveData<List<AreaWithTasks>>

    @Insert(onConflict =OnConflictStrategy.ABORT )
    @Throws(SQLiteConstraintException::class)
    suspend fun insert(area: Area)

    @Insert(onConflict =OnConflictStrategy.ABORT )
    @Throws(SQLiteConstraintException::class)
    fun insertNow(area: Area)

    @Delete
    suspend fun delete(area: Area)

    @Update(onConflict =OnConflictStrategy.ABORT )
    @Throws(SQLiteConstraintException::class)
    suspend fun update(area: Area)
}