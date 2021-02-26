package com.dhbw.cas.integra.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SprintDao {
    @Query("SELECT * FROM sprints ORDER BY id ASC")
    fun getSprints(): LiveData<List<Sprint>>

    @Transaction
    @Query("SELECT * FROM sprints WHERE isActive = 1 LIMIT 1")
    fun getActiveSprintWithTasks(): SprintWithTasks

    @Transaction
    @Query("SELECT * FROM sprints WHERE isActive = 1 LIMIT 1")
    fun getLiveActiveSprintWithTasks(): LiveData<SprintWithTasks>

    @Insert
    suspend fun insert(sprint: Sprint): Long

    @Delete
    suspend fun delete(sprint: Sprint)

    @Update
    suspend fun update(sprint: Sprint)
}