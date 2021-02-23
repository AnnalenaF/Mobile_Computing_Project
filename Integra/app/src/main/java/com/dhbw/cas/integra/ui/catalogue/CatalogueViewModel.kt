package com.dhbw.cas.integra.ui.catalogue

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dhbw.cas.integra.AppDatabase
import kotlinx.coroutines.launch
import java.sql.Time

class CatalogueViewModel(application: Application) : AndroidViewModel(application) {
    private var taskDao = AppDatabase.getDatabase(application).taskDao()

    val tasks = taskDao.getTasks()

    fun createTask(title: String, description: String, priority: Int, area_id: Long, expectedDuration: Time)
            = viewModelScope.launch { taskDao.insert(Task(title, description, priority, area_id, expectedDuration)) }

    fun updateTask(task: Task) = viewModelScope.launch { taskDao.update(task) }

    fun deleteTask(task: Task) = viewModelScope.launch { taskDao.delete(task) }
}