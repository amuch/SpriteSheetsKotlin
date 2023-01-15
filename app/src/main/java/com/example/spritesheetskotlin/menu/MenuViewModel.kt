package com.example.spritesheetskotlin.menu

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MenuViewModel : ViewModel() {
    val nameProject: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }
}