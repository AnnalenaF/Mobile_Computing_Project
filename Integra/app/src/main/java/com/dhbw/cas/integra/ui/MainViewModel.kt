package com.dhbw.cas.integra.ui

import android.app.Application
import androidx.lifecycle.*
import com.dhbw.cas.integra.AppDatabase
import com.dhbw.cas.integra.data.Area
import com.dhbw.cas.integra.data.Sprint
import com.dhbw.cas.integra.data.Task
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private var sprintDao = AppDatabase.getDatabase(application).sprintDao()
    private var areaDao = AppDatabase.getDatabase(application).areaDao()
    private var taskDao = AppDatabase.getDatabase(application).taskDao()

    // areas -------------------------------------------------------------------------------------
    val areas = areaDao.getAreas()
    val areasWithTasks = areaDao.getAreasWithTasks()

    fun createArea(text: String, label: Int)
            = viewModelScope.launch { areaDao.insert(Area(text, label)) }

    fun updateArea(area: Area) = viewModelScope.launch { areaDao.update(area) }

    fun deleteArea(area: Area) = viewModelScope.launch { areaDao.delete(area) }

    fun getAreaTexts() = areaDao.getAreaTexts()

    // tasks -------------------------------------------------------------------------------------
    val tasks = taskDao.getTasks()

    fun createTask(title: String, description: String, priority: Int, area_text: String,
                   expectedDuration: Int)
            = viewModelScope.launch { taskDao.insert(Task(title, description, priority, area_text,
        expectedDuration, 0, null)) }

    fun updateTask(task: Task) = viewModelScope.launch { taskDao.update(task) }

    fun deleteTask(task: Task) = viewModelScope.launch { taskDao.delete(task) }

    // sprint (with tasks)------------------------------------------------------------------------
    val activeSprint: Sprint? = sprintDao.getActiveSprint()
    val activeSprintLive = sprintDao.getLiveActiveSprintWithTasks()
    fun createSprintWithTasks(startDate: Long, endDate: Long, tasks: List<Task>) =
        viewModelScope.launch {
            val sprint = Sprint(1, startDate, endDate)
            val sprintId = sprintDao.insert(sprint)
            for (task in tasks){
                task.sprintId = sprintId
                taskDao.update(task)
            }
        }

    fun deleteSprint(sprint: Sprint) = viewModelScope.launch {
        sprintDao.delete(sprint)
    }

    fun deleteSprintTasksByState(sprintId: Long, state: Int) = viewModelScope.launch {
        taskDao.deleteByIdAndState(sprintId, state)
    }

    fun resetTaskStates() = viewModelScope.launch {
        taskDao.resetStates()
    }
}