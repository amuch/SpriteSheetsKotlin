package com.example.spritesheetskotlin.bitmap

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.toColor
import com.example.spritesheetskotlin.palette.COLOR_CLEAR
import com.example.spritesheetskotlin.palette.Palette
import kotlin.math.roundToInt

class BitmapManager(var bitmapList: ArrayList<Bitmap>) {
    constructor() : this(ArrayList<Bitmap>(1))

    fun createBitmap(width: Int, height: Int) : Bitmap {
        val config = Bitmap.Config.ARGB_8888
        return Bitmap.createBitmap(width, height, config)
    }

    fun colorPixel(index: Int, point: Point, color: Int) {
        val bitmap = bitmapList[index]
        bitmap.setPixel(point.x, point.y, color)
    }

    fun colorPixel(bitmap: Bitmap?, point: Point, color: Int?) {
        if (color != null) {
            bitmap?.setPixel(point.x, point.y, color)
        }
    }

    fun fillBitmap(bitmap: Bitmap?, color: Int?) {
        for(i in 0 until bitmap!!.width) {
            for(j in 0 until bitmap.height) {
                bitmap.setPixel(i, j, color!!)
            }
        }
    }

    fun pointMatchesClear(bitmap: Bitmap?, point: Point) : Boolean {
        val color = bitmap!!.getPixel(point.x, point.y)
        if(Color.red(color) != 0) {
            println("RED: ${Color.red(color)}")
            return false
        }
        if(Color.green(color) != 0) {
            println("GREEN: ${Color.green(color)}")
            return false
        }
        if(Color.blue(color) != 0) {
            println("BLUE: ${Color.blue(color)}")
            return false
        }
        if(Color.alpha(color) != 0) {
            println("ALPHA: ${Color.alpha(color)}")
            return false
        }

        return true
    }

    fun fillBitmapPartial(bitmap: Bitmap?, point: Point, color: Int?) {
        val pointsFill = ArrayList<Point>(0)

        if(pointMatchesClear(bitmap!!, point)) {
//        if(bitmap!!.getPixel(point.x, point.y).toColor().equals(COLOR_CLEAR)) {

//            colorPixel(bitmap, point, color)
            val resolution: Int = 16
            projectColor(bitmap, point, resolution, color)

            // Left
            if(point.x > 0) {
                val pointLeft = Point(point.x - 1, point.y)
                if(pointMatchesClear(bitmap, pointLeft)) {
                    pointsFill.add(pointLeft)
                }
            }
            // Top
            if(point.y > 0) {
                val pointTop = Point(point.x, point.y - 1)
                if(pointMatchesClear(bitmap, pointTop)) {
                    pointsFill.add(pointTop)
                }
            }
            // Right
            if(point.x < bitmap.width) {
                val pointRight = Point(point.x + 1, point.y)
                if(pointMatchesClear(bitmap, pointRight)) {
                    pointsFill.add(pointRight)
                }
            }
            // Bottom
            if(point.y < bitmap.height) {
                val pointBottom = Point(point.x, point.y + 1)
                if(pointMatchesClear(bitmap, pointBottom)) {
                    pointsFill.add(pointBottom)
                }
            }
        }

        if(pointsFill.size > 0) {
            for (i in 0 until pointsFill.size) {
                fillBitmapPartial(bitmap, pointsFill.get(i), color)
            }
        }
        pointsFill.clear()
    }

    fun getStoragePixel(view: View, event: MotionEvent, index: Int): Point {
        val bitmap = bitmapList[index]
        var x: Int = event.x.roundToInt() * bitmap.width / view.width
        var y: Int = event.y.roundToInt()  * bitmap.height / view.height

        if(x < 0) x = 0
        else if(x > bitmap.width - 1) x = bitmap.width - 1

        if(y < 0) y = 0
        else if(y > bitmap.height - 1) y = bitmap.height - 1

        return Point(x, y)
    }

    fun projectColor(bitmap: Bitmap?, point: Point, resolution: Int, color: Int?) {
        val startX: Int = point.x * resolution
        val startY: Int = point.y * resolution

        for(i in startX until startX + resolution) {
            for(j in startY until startY + resolution) {
                val innerPoint = Point(i, j)
                colorPixel(bitmap, innerPoint, color)
            }
        }
    }

    fun projectBitmap(index: Int, resolution: Int) : Bitmap {
        val sourceBitmap = bitmapList[index]
        val config = Bitmap.Config.ARGB_8888
        val projectedBitmap = Bitmap.createBitmap(sourceBitmap.width * resolution, sourceBitmap.height * resolution, config)

        for(i in 0 until projectedBitmap.width) {
            for(j in 0 until projectedBitmap.height) {
                projectedBitmap.setPixel(i, j, sourceBitmap.getPixel(i / resolution, j / resolution))
            }
        }

        return projectedBitmap
    }
}