package com.dhbw.cas.integra.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.dhbw.cas.integra.data.Task

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY id ASC")
    fun getTasks(): LiveData<List<Task>>

    @Insert
    suspend fun insert(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Update
    suspend fun update(task: Task)
}