package com.dhbw.cas.integra.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dhbw.cas.integra.AppDatabase

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private var sprintDao = AppDatabase.getDatabase(application).sprintDao()

    val activeSprint = sprintDao.getActiveSprintWithTasks()
}