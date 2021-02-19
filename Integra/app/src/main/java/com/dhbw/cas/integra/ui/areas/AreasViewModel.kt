package com.dhbw.cas.integra.ui.areas

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AreasViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is areas Fragment"
    }
    val text: LiveData<String> = _text
}