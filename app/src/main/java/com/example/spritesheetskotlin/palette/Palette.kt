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
import com.example.spritesheetskotlin.R

const val NAME_CLEAR_COLOR: String = "clear"
const val COLOR_CLEAR = 0x00000000
const val COLOR_BUTTON_ACTIVE = Color.YELLOW
const val COLOR_BUTTON_INACTIVE = Color.GRAY

class Palette(var dbColorList: ArrayList<DBColor>) {
    constructor() : this(ArrayList<DBColor>(1))
    fun getColorByIndex(index: Int) : Int {
        return dbColorList[index % dbColorList.size].color
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

    fun createTestPalette() {
        val black = DBColor(8, "black", 0x000000, 1)
        this.dbColorList.add(black)

        val darkGray = DBColor(10, "darkGray", 0x444444, 1)
        this.dbColorList.add(darkGray)

        val silver = DBColor(18, "silver", 0x9897A9, 1)
        this.dbColorList.add(silver)

        val pink : DBColor = DBColor(15, "pink", 0xFF69B4, 1)
        this.dbColorList.add(pink)

        val red : DBColor = DBColor(1, "red", 0xFF0000, 1)
        this.dbColorList.add(red)

        val maroon : DBColor = DBColor(14, "maroon", 0x5F0000, 1)
        this.dbColorList.add(maroon)

        val orange: DBColor = DBColor(2, "orange", 0xFFA500, 1)
        this.dbColorList.add(orange)

        val yellow = DBColor(3, "yellow", 0xFFFF00, 1)
        this.dbColorList.add(yellow)

        val green = DBColor(4, "green", 0x00FF00, 1)
        this.dbColorList.add(green)

        val hunterGreen = DBColor(13, "hunterGreen", 0x003314, 1)
        this.dbColorList.add(hunterGreen)

        val blue = DBColor(5, "blue", 0x0000FF, 1)
        this.dbColorList.add(blue)

        val skyBlue = DBColor(12, "skyBlue", 0x87CEEB, 1)
        this.dbColorList.add(skyBlue)

        val purple = DBColor(6, "purple", 0x800080, 1)
        this.dbColorList.add(purple)

        val brown = DBColor(7, "brown", 0x8B4513, 1)
        this.dbColorList.add(brown)

        val tan = DBColor(16, "tan", 0xCDA789, 1)
        this.dbColorList.add(tan)

        val gold = DBColor(17, "gold", 0xFFD868, 1)
        this.dbColorList.add(gold)

        val lightGray = DBColor(11, "lightGray", 0xBBBBBB, 1)
        this.dbColorList.add(lightGray)

        val white = DBColor(9, "white", 0xFFFFFF, 1)
        this.dbColorList.add(white)
    }
}