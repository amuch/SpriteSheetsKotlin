package com.example.spritesheetskotlin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class DrawingViewModelFactory(private val width: Int, private val height: Int, private val resolution: Int)
    : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(DrawingViewModel::class.java)) {
                return DrawingViewModel(width, height, resolution) as T
            }
            throw IllegalArgumentException("Unknown View Model Class")
    }
}