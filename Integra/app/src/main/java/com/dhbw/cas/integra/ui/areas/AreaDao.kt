package com.dhbw.cas.integra.ui.areas

import androidx.lifecycle.LiveData
import androidx.room.*
import org.w3c.dom.Text

@Dao
interface AreaDao {
    @Query("SELECT * FROM areas ORDER BY id ASC")
    fun getAreas(): LiveData<List<Area>>

    @Query("SELECT * FROM areas WHERE text = text")
    fun getAreaByText(text: String): List<Area>

    @Query("SELECT * FROM areas WHERE label = label")
    fun getAreaByLabel(label: Int): List<Area>

    @Insert
    suspend fun insert(area: Area)

    @Insert
    fun insertNow(area: Area)

    @Delete
    suspend fun delete(area: Area)

    @Update
    suspend fun update(area: Area)
}