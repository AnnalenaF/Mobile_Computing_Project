package com.dhbw.cas.integra.ui.areas

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AreaDao {
    @Query("SELECT * FROM areas ORDER BY id ASC")
    fun getAreas(): LiveData<List<Area>>

    @Insert
    suspend fun insert(area: Area)

    @Delete
    suspend fun delete(area: Area)

    @Update
    suspend fun update(area: Area)
}