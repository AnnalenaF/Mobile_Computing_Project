package com.dhbw.cas.integra.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dhbw.cas.integra.AppDatabase

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private var sprintDao = AppDatabase.getDatabase(application).sprintDao()
    private var areaDao = AppDatabase.getDatabase(application).areaDao()

    val activeSprint = sprintDao.getActiveSprintWithTasks()
    val areas = areaDao.getAreas()
}