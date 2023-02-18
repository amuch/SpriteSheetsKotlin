package com.example.spritesheetskotlin

import android.graphics.Bitmap
import android.graphics.Point
import android.widget.Button
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DrawingViewModel(width: Int, height: Int, resolution: Int, color: Int) : ViewModel() {
    val bitmapMain = MutableLiveData<Bitmap>(Bitmap.createBitmap(resolution * width, resolution * height, Bitmap.Config.ARGB_8888))
    val bitmapStorage = MutableLiveData<Bitmap>(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888))
    val currentColor: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(color)
    }

//    val booleanArray = MutableLiveData() { Array(width){ BooleanArray(height)} }
}