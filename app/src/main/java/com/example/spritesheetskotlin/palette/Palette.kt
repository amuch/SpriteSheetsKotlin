package com.example.spritesheetskotlin.palette

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.widget.Button
import android.widget.ImageButton
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.example.spritesheetskotlin.DrawingActivity
import com.example.spritesheetskotlin.R
import com.example.spritesheetskotlin.database.Database
import com.example.spritesheetskotlin.database.NOT_FOUND

const val COLOR_CLEAR : Long = 0x00000000

class Palette(var dbColorList: ArrayList<DBColor>) {
    constructor() : this(ArrayList<DBColor>(1))

    fun getColorByIndex(index: Int) : Long {
        if(dbColorList.size > 0) {
            return dbColorList[index % dbColorList.size].color
        }
        return Color.BLACK.toLong()
    }

    fun colorButton(color: Int, button: Button) {
        val red = color.red
        val green = color.green
        val blue = color.blue
        val textColor: Int = if((red + green + blue) < (127 * 3)) Color.WHITE else Color.BLACK
        button.setTextColor(textColor)
        button.setBackgroundColor(color)
    }

    fun blendedColor(colorPixel: Int, colorCurrent: Int) : Int {
        val red = (colorPixel.red + colorCurrent.red) / 2
        val green = (colorPixel.green + colorCurrent.green) / 2
        val blue = (colorPixel.blue + colorCurrent.blue) / 2

        return Color.argb(0xFF, red, green, blue)
    }

    fun colorImageButton(color: Int, imageButton: ImageButton, resources: Resources) {
        imageButton.setBackgroundColor(color)

        val red = color.red
        val green = color.green
        val blue = color.blue
        val bitmap: Bitmap = BitmapFactory.decodeResource(
            resources,
            if((red + green + blue) < (127 * 3)) R.mipmap.palette_white_full else R.mipmap.palette_black_full
        )
        imageButton.setImageBitmap(bitmap)
    }

    fun addColor(color: Long) {
        val dbColor = DBColor(20, "test", color, 1)
        this.dbColorList.add(dbColor)
    }

    fun updateColor(oldColor: Long, newColor: Long) {
        for(i in 0 until this.dbColorList.size) {
            if(dbColorList[i].color == oldColor) {
                dbColorList[i].color = newColor
            }
        }
    }

    fun deleteColor(color: Long) {
        for(i in 0 until this.dbColorList.size) {
            if(dbColorList[i].color == color) {
                dbColorList.removeAt(i)
                break
            }
        }
    }

    fun loadPalette(id: Int) {
        val dbPalette = DrawingActivity.database.readPalette(id)
        if(!(NOT_FOUND == dbPalette.name)) {
            val dbColors = DrawingActivity.database.readColors(dbPalette.id)
            for(i in 0 until dbColors.size) {
                this.dbColorList.add(dbColors[i])
            }
        }
    }

    fun initializePalette(id: Int) {
        loadPalette(id)
        if(0 == this.dbColorList.size) {
            println("No colors in selected palette.")
            loadDefaultPalette()
        }
    }


    fun loadDefaultPalette() {
        val black = DBColor(8, "black", 0xFF000000, 1)
        this.dbColorList.add(black)

        val darkGray = DBColor(10, "darkGray", 0xFF444444, 1)
        this.dbColorList.add(darkGray)

        val silver = DBColor(18, "silver", 0xFF9897A9, 1)
        this.dbColorList.add(silver)

        val pink : DBColor = DBColor(15, "pink", 0xFFFF69B4, 1)
        this.dbColorList.add(pink)

        val red : DBColor = DBColor(1, "red", 0xFFFF0000, 1)
        this.dbColorList.add(red)

        val maroon : DBColor = DBColor(14, "maroon", 0xFF5F0000, 1)
        this.dbColorList.add(maroon)

        val orange: DBColor = DBColor(2, "orange", 0xFFFFA500, 1)
        this.dbColorList.add(orange)

        val yellow = DBColor(3, "yellow", 0xFFFFFF00, 1)
        this.dbColorList.add(yellow)

        val green = DBColor(4, "green", 0xFF00FF00, 1)
        this.dbColorList.add(green)

        val hunterGreen = DBColor(13, "hunterGreen", 0xFF003314, 1)
        this.dbColorList.add(hunterGreen)

        val blue = DBColor(5, "blue", 0xFF0000FF, 1)
        this.dbColorList.add(blue)

        val skyBlue = DBColor(12, "skyBlue", 0xFF87CEEB, 1)
        this.dbColorList.add(skyBlue)

        val purple = DBColor(6, "purple", 0xFF800080, 1)
        this.dbColorList.add(purple)

        val brown = DBColor(7, "brown", 0xFF8B4513, 1)
        this.dbColorList.add(brown)

        val tan = DBColor(16, "tan", 0xFFCDA789, 1)
        this.dbColorList.add(tan)

        val gold = DBColor(17, "gold", 0xFFFFD868, 1)
        this.dbColorList.add(gold)

        val lightGray = DBColor(11, "lightGray", 0xFFCCCCCC, 1)
        this.dbColorList.add(lightGray)

        val white = DBColor(9, "white", 0xFFFFFFFF, 1)
        this.dbColorList.add(white)
    }

    fun colorInt(color: Long) : Int {
        return ((color and 0xFFFFFFFF).toInt())
    }
}