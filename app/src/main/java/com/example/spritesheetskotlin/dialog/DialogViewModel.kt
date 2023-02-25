package com.example.spritesheetskotlin.dialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DialogViewModel : ViewModel() {
    val dialogVisible: MutableLiveData<DialogVisibleEnum> by lazy {
        MutableLiveData<DialogVisibleEnum>()
    }
}