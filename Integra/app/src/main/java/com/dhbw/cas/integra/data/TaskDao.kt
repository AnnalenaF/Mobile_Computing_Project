package com.dhbw.cas.integra.data

import androidx.lifecycle.LiveData
import androidx.room.*

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

    @Query ("DELETE FROM tasks WHERE sprintId = :sprintId AND state = :state")
    suspend fun deleteByIdAndState(sprintId: Long, state: Int)

    @Query ("UPDATE tasks SET state = 0")
    suspend fun resetStates()
}