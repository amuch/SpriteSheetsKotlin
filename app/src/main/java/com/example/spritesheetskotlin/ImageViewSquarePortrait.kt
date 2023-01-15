package com.example.spritesheetskotlin

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

@SuppressLint("AppCompatCustomView")
class ImageViewSquarePortrait(context: Context?, attrs: AttributeSet?) : ImageView(context, attrs)  {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width:Int = measuredWidth
        setMeasuredDimension(width, width)
    }
}