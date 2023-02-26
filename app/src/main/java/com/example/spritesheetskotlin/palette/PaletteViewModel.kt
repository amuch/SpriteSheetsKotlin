package com.example.spritesheetskotlin.palette

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PaletteViewModel(palette: Palette) : ViewModel() {
    val palette = MutableLiveData<Palette>(palette)
    val currentColor = MutableLiveData<Long>(palette.getColorByIndex(0))
}