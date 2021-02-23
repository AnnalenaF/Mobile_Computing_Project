package com.dhbw.cas.integra.ui.catalogue

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CatalogueViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is catalogue Fragment"
    }
    val text: LiveData<String> = _text
}