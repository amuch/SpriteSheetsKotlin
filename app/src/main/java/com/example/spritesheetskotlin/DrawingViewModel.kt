package com.example.spritesheetskotlin

import android.graphics.Bitmap
import android.graphics.Point
import android.widget.Button
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.spritesheetskotlin.bitmap.BitmapActionEnum
import com.example.spritesheetskotlin.bitmap.BitmapManager

class DrawingViewModel(width: Int, height: Int, resolution: Int) : ViewModel() {
    val bitmapMain = MutableLiveData<Bitmap>(Bitmap.createBitmap(resolution * width, resolution * height, Bitmap.Config.ARGB_8888))
    val bitmapManager: MutableLiveData<BitmapManager> by lazy {
        MutableLiveData<BitmapManager>().apply {
            value = BitmapManager()
        }
    }
//    val bitmapManager = MutableLiveData<BitmapManager>(BitmapManager())
//    val bitmapStorage = MutableLiveData<Bitmap>(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888))
//    val bitmapList = MutableLiveData<ArrayList<Bitmap>>(ArrayList<Bitmap>(1))
//    val bitmapIndex = MutableLiveData<Int>(0)
//    val bitmapList: MutableLiveData<ArrayList<Bitmap>> by lazy {
//        MutableLiveData<ArrayList<Bitmap>>()
//    }
//    val booleanArray = MutableLiveData() { Array(width){ BooleanArray(height)} }
    operator fun <T> MutableLiveData<ArrayList<T>>.plusAssign(values: List<T>) {
        val value = this.value ?: arrayListOf()
        this.value = value
    }
}