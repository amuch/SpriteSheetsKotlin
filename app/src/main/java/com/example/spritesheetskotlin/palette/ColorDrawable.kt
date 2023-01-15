package com.example.spritesheetskotlin.palette

import android.graphics.*
import android.graphics.drawable.Drawable

class ColorDrawable(color: Int): Drawable() {
    private val red = Color.red(color)
    private val green = Color.green(color)
    private val blue = Color.blue(color)
    private val paint: Paint = Paint().apply { setARGB(0xFF, red, green, blue)}


    override fun draw(canvas: Canvas) {
        val width: Int = bounds.width()
        val height: Int = bounds.height()
        val radius: Float = Math.min(width, height).toFloat() / 2f
        var backgroundColor = Color.WHITE
        if((red + green + blue) > (3 * 127))
            backgroundColor = Color.BLACK
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4.0f
        paint.color = backgroundColor
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius + 2, paint)
        paint.style = Paint.Style.FILL
        paint.color = Color.argb(0xFF, red, green, blue)
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, paint)
    }

    override fun setAlpha(alpha: Int) {
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
    }

    override fun getOpacity(): Int =
        // Must be PixelFormat.UNKNOWN, TRANSLUCENT, TRANSPARENT, or OPAQUE
        PixelFormat.OPAQUE

    // Reference: https://developer.android.com/guide/topics/graphics/drawables
}