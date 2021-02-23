package com.dhbw.cas.integra.ui.catalogue

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhbw.cas.integra.AppDatabase
import com.dhbw.cas.integra.ui.areas.Area
import kotlinx.coroutines.launch
import java.sql.Timestamp

class CatalogueViewModel(application: Application) : ViewModel() {
    private var taskDao = AppDatabase.getDatabase(application).taskDao()

    val areas = taskDao.getTasks()

    fun createTask(text: String, priority: Int, area_id: Long, expectedDuration: Timestamp)
            = viewModelScope.launch { taskDao.insert(Task(text, priority, area_id, expectedDuration)) }

    fun updateTask(task: Task) = viewModelScope.launch { taskDao.update(task) }

    fun deleteTask(task: Task) = viewModelScope.launch { taskDao.delete(task) }
}