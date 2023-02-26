package com.example.spritesheetskotlin.palette

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class PaletteViewModelFactory(private val palette: Palette): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PaletteViewModel::class.java)) {
            return PaletteViewModel(palette) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }
}