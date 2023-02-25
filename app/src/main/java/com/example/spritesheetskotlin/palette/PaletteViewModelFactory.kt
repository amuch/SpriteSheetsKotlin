package com.example.spritesheetskotlin.palette

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.spritesheetskotlin.DrawingViewModel
import com.example.spritesheetskotlin.database.Database
import java.lang.IllegalArgumentException

class PaletteViewModelFactory(private val palette: Palette): ViewModelProvider.Factory {
//class PaletteViewModelFactory(private val name: String, private val database: Database): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(PaletteViewModel::class.java)) {
//            return PaletteViewModel(name, database) as T
            return PaletteViewModel(palette) as T
        }
        throw IllegalArgumentException("Unknown View Model Class")
    }
}