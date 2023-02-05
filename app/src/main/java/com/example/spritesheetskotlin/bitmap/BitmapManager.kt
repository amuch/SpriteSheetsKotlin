package com.example.spritesheetskotlin.bitmap

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.view.MotionEvent
import android.view.View
import kotlin.math.roundToInt

class BitmapManager(var bitmapList: ArrayList<Bitmap>) {
    constructor() : this(ArrayList<Bitmap>(1))
    var indexCurrent: Int = 0

    fun createBitmap(width: Int, height: Int) : Bitmap {
        val config = Bitmap.Config.ARGB_8888
        return Bitmap.createBitmap(width, height, config)
    }

    fun colorPixel(index: Int, point: Point, color: Int?) {
        val bitmap = bitmapList[index]
        bitmap.setPixel(point.x, point.y, color!!)
    }

    fun colorPixel(bitmap: Bitmap?, point: Point, color: Int?) {
        bitmap?.setPixel(point.x, point.y, color!!)
    }

    fun fillBitmap(bitmap: Bitmap?, color: Int?) {
        for(i in 0 until bitmap!!.width) {
            for(j in 0 until bitmap.height) {
                bitmap.setPixel(i, j, color!!)
            }
        }
    }

    fun pointHasZeroAlpha(index: Int, point: Point) : Boolean {
        val color = bitmapList[index].getPixel(point.x, point.y)
        if(Color.alpha(color) != 0) {
            return false
        }
        return true
    }

    fun fillShouldOverwrite(index: Int, point: Point, color: Int?) : Boolean {
        val colorsMatch = color == bitmapList[index].getPixel(point.x, point.y)
        return pointHasZeroAlpha(index, point) || colorsMatch
    }

    fun fillBitmapPartial(bitmap: Bitmap?, index: Int, point: Point, resolution: Int, color: Int?) {
        if(pointHasZeroAlpha(index, point)) {
            colorPixel(bitmapList[index], point, color)
            projectColor(bitmap, point, resolution, color)

            val pointLeft = Point(point.x - 1, point.y)
            if(!(pointLeft.x < 0)) {
                if(pointHasZeroAlpha(index, pointLeft)) {
                    fillBitmapPartial(bitmap, index, pointLeft, resolution, color)
                }
            }

            val pointTop = Point(point.x, point.y - 1)
            if(!(pointTop.y < 0)) {
                if(pointHasZeroAlpha(index, pointTop)) {
                    fillBitmapPartial(bitmap, index, pointTop, resolution, color)
                }
            }

            val pointRight = Point(point.x + 1, point.y)
            if(pointRight.x < bitmapList[index].width) {
                if(pointHasZeroAlpha(index, pointRight)) {
                    fillBitmapPartial(bitmap, index, pointRight, resolution, color)
                }
            }

            val pointBottom = Point(point.x, point.y + 1)
            if(pointBottom.y < bitmapList[index].height) {
                if(pointHasZeroAlpha(index, pointBottom)) {
                    fillBitmapPartial(bitmap, index, pointBottom, resolution, color)
                }
            }
        }
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