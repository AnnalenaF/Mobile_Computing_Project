package com.dhbw.cas.integra.ui.areas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dhbw.cas.integra.R

class AreasViewModel : ViewModel() {
    val areas : MutableList<Area> = mutableListOf(
        Area(text = "Privat", label = R.drawable.shape_area_label_0),
        Area(text = "Arbeit", label = R.drawable.shape_area_label_1),
        /*Area(text = "Lebensbereich 3", label = R.drawable.shape_area_label_2),
        Area(text = "Lebensbereich 4", label = R.drawable.shape_area_label_3),
        Area(text = "Lebensbereich 5", label = R.drawable.shape_area_label_4),
        Area(text = "Lebensbereich 6", label = R.drawable.shape_area_label_5),
        Area(text = "Lebensbereich 7", label = R.drawable.shape_area_label_6),
        Area(text = "Lebensbereich 8", label = R.drawable.shape_area_label_7),
        Area(text = "Lebensbereich 9", label = R.drawable.shape_area_label_8),
        Area(text = "Lebensbereich 10", label = R.drawable.shape_area_label_9)*/)
}