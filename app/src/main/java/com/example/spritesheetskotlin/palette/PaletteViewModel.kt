package com.example.spritesheetskotlin.palette

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spritesheetskotlin.DrawingActivity
import com.example.spritesheetskotlin.database.Database

//class PaletteViewModel(name: String, database: Database) : ViewModel() {
class PaletteViewModel(palette: Palette) : ViewModel() {
//    val dbPalette = database.readPalette(name)
//    val paletteLocal = Palette()
//    val database = MutableLiveData<Database>(database)
    val palette = MutableLiveData<Palette>(palette)
    val currentColor = MutableLiveData<Long>(palette.getColorByIndex(0))
//    val currentColor: MutableLiveData<Int> by lazy {
//
//        MutableLiveData<Int>(palette.getColorByIndex(0))
//    }

}