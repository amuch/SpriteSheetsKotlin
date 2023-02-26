package com.example.spritesheetskotlin.customviews

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import java.lang.Integer.min

@SuppressLint("AppCompatCustomView")
class ImageViewSquareMinimumSide(context: Context?, attrs: AttributeSet?)  : ImageView(context, attrs)  {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val side = min(measuredWidth, measuredHeight)
        setMeasuredDimension(side, side)
    }
}