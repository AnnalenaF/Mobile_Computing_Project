package com.dhbw.cas.integra.ui.areas

import android.app.Application
import androidx.lifecycle.*
import com.dhbw.cas.integra.AppDatabase
import com.dhbw.cas.integra.data.Area
import kotlinx.coroutines.launch

class AreasViewModel(application: Application) : AndroidViewModel(application) {
    private var areaDao = AppDatabase.getDatabase(application).areaDao()

    val areas = areaDao.getAreas()

    fun createArea(text: String, label: Int)
        = viewModelScope.launch { areaDao.insert(Area(text, label)) }

    fun updateArea(area: Area) = viewModelScope.launch { areaDao.update(area) }

    fun deleteArea(area: Area) = viewModelScope.launch { areaDao.delete(area) }

    fun getAreaTexts() = areaDao.getAreaTexts()

    fun getLabelByText(text: String) = areaDao.getLabelByText(text)
}