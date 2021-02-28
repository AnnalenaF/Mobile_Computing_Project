package com.dhbw.cas.integra.ui.sprint

import android.app.Application
import androidx.lifecycle.*
import com.dhbw.cas.integra.AppDatabase
import com.dhbw.cas.integra.data.Sprint
import com.dhbw.cas.integra.data.Task
import kotlinx.coroutines.launch

class SprintViewModel(application: Application) : AndroidViewModel(application) {
    private var sprintDao = AppDatabase.getDatabase(application).sprintDao()
    private var areaDao = AppDatabase.getDatabase(application).areaDao()
    private var taskDao = AppDatabase.getDatabase(application).taskDao()

    val activeSprint: Sprint? = sprintDao.getActiveSprint()
    val activeSprintLive = sprintDao.getLiveActiveSprintWithTasks()
    val areas = areaDao.getAreas()
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