package com.example.spritesheetskotlin.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

@SuppressLint("AppCompatCustomView")
class ImageViewSquareLandscape(context: Context?, attrs: AttributeSet?) : ImageView(context, attrs)  {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val height:Int = measuredHeight
        setMeasuredDimension(height, height)
    }
}