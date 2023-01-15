package com.example.spritesheetskotlin.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.*
import com.example.spritesheetskotlin.R
import com.example.spritesheetskotlin.palette.ColorDrawable
import com.example.spritesheetskotlin.palette.NAME_CLEAR_COLOR
import com.example.spritesheetskotlin.palette.Palette
import kotlin.math.max

class DialogManagePalette(activity: Activity, palette: Palette): Dialog(activity) {
    private var palette: Palette
    private var activity: Activity
    private lateinit var buttonCancel: Button
    private lateinit var buttonAccept: Button
    private lateinit var textViewRGB: TextView
    private lateinit var textViewHex: TextView
    private lateinit var seekBarRed: SeekBar
    private lateinit var seekBarGreen: SeekBar
    private lateinit var seekBarBlue: SeekBar

    init {
        setCancelable(false)
        palette.also { this.palette = it }
        activity.also { this.activity = it }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_palette_manager)

        bindUI()
    }



    private fun bindUI() {
        textViewRGB = findViewById<TextView>(R.id.textViewDialogColorRGB)
        textViewHex = findViewById<TextView>(R.id.textViewDialogColorHex)

        buttonCancel = findViewById<Button>(R.id.dialogManagePaletteButtonCancel)
        buttonCancel.setOnClickListener {
            this.cancel()
        }

        buttonAccept = findViewById<Button>(R.id.dialogManagePaletteButtonAccept)
        buttonAccept.setOnClickListener {

        }

        seekBarRed = findViewById(R.id.seekBarRed)
        seekBarRed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, p1: Int, p2: Boolean) {

            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })
        seekBarGreen = findViewById(R.id.seekBarGreen)
        seekBarGreen.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, p1: Int, p2: Boolean) {

            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })
        seekBarBlue = findViewById(R.id.seekBarBlue)
        seekBarBlue.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, p1: Int, p2: Boolean) {

            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })

        displayPalette()
    }

    private fun displayPalette() {
        val linearLayout: LinearLayout = findViewById(R.id.dialogPaletteColors)
        linearLayout.removeAllViews()
        val longest = max(activity.resources.displayMetrics.widthPixels, activity.resources.displayMetrics.heightPixels)
        val side = 30 * longest / (250)
        val margin = longest / (250)

        for(i in 0 until palette.dbColorList.size) {
            val imageButton = ImageButton(activity.applicationContext).apply {
                setBackgroundResource(R.mipmap.background16)
                setImageDrawable(ColorDrawable(palette.dbColorList[i].color))
                layoutParams = colorImageParameters(side, side, margin)
                setOnClickListener{
                    val red = Color.red(palette.dbColorList[i].color)
                    seekBarRed.progress = red
                    val green = Color.green(palette.dbColorList[i].color)
                    seekBarGreen.progress = green
                    val blue = Color.blue(palette.dbColorList[i].color)
                    seekBarBlue.progress = blue
                    val alpha = when (palette.dbColorList[i].name) {
                        NAME_CLEAR_COLOR -> 0
                        else -> 0xFF
                    }
                    textViewRGB.text = "rgb($red, $green, $blue)"
                    val formatTemplate = "#%02X%02X%02X"
                    textViewHex.text = formatTemplate.format(red, green, blue)
                    Palette().colorButton(Color.argb(alpha, red, green, blue), buttonAccept)
                }
            }
            linearLayout.addView(imageButton)
        }

    }

    private fun colorImageParameters(width: Int, height: Int, margin: Int) : LinearLayout.LayoutParams {
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER
        params.setMargins(margin, margin, margin, margin)
        params.width = width
        params.height = height

        return params
    }
}