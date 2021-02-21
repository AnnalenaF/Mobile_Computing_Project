package com.dhbw.cas.integra.ui.areas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dhbw.cas.integra.R

class AreasViewModel : ViewModel() {

    val areas : MutableList<Area> = mutableListOf(
        Area(text = "Privat", label = R.drawable.shape_area_label_0),
        Area(text = "Arbeit", label = R.drawable.shape_area_label_1))

    fun createArea(text: String, label: Int) {
        val area = Area(text, label)
        areas.add(area)
    }
}