package com.example.spritesheetskotlin.bitmap

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BitmapViewModel : ViewModel() {
    val bitmapAction: MutableLiveData<BitmapActionEnum> by lazy {
        MutableLiveData<BitmapActionEnum>()
    }
}