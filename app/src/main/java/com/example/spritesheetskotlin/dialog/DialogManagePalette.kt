package com.example.spritesheetskotlin.dialog

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.widget.*
import androidx.core.graphics.toColorInt
import com.example.spritesheetskotlin.DrawingActivity
import com.example.spritesheetskotlin.R
import com.example.spritesheetskotlin.palette.ColorDrawable
import com.example.spritesheetskotlin.palette.Palette
import kotlin.math.max

class DialogManagePalette(activity: DrawingActivity, palette: Palette): Dialog(activity) {
    private var palette: Palette
    private var activity: DrawingActivity
    private lateinit var buttonCancel: Button
    private lateinit var buttonAccept: Button
    private lateinit var textViewRGB: TextView
    private lateinit var textViewHex: TextView
    private lateinit var seekBarRed: SeekBar
    private lateinit var seekBarGreen: SeekBar
    private lateinit var seekBarBlue: SeekBar
    private var red: Int = 0
    private var green: Int = 0
    private var blue: Int = 0
    private var name: String = ""

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

    private fun setButtonAcceptColor() {
        var textColor = Color.BLACK
        if((red + green + blue) < (127 * 3)) {
            textColor = Color.WHITE
        }
        buttonAccept.setTextColor(textColor)
        buttonAccept.setBackgroundColor(Color.argb(0xFF, red, green, blue))
    }

    private fun setTextViewRGB() {
        textViewRGB.text = "rgb($red, $green, $blue)"
    }

    private fun setTextViewHex() {
        val formatTemplate = "#%02X%02X%02X"
        name = formatTemplate.format(red, green, blue)
        textViewHex.text = name
    }

    private fun setDynamicUIElements() {
        setButtonAcceptColor()
        setTextViewRGB()
        setTextViewHex()
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

            val dbPalette = DrawingActivity.database.readPalette(DrawingActivity.namePalette)

            val color = Color.argb(0xFF, red, green, blue)
            if(!DrawingActivity.database.colorIsInPalette(color.toLong(), dbPalette.id)) {
                DrawingActivity.database.createColor(name, color.toLong(), dbPalette.id)
                palette.addColor(color.toLong())
                activity.displayPalette()
            }

            this.cancel()
        }

        seekBarRed = findViewById(R.id.seekBarRed)
        seekBarRed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, p2: Boolean) {
                red = progress
                setDynamicUIElements()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })
        seekBarGreen = findViewById(R.id.seekBarGreen)
        seekBarGreen.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, p2: Boolean) {
                green = progress
                setDynamicUIElements()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })
        seekBarBlue = findViewById(R.id.seekBarBlue)
        seekBarBlue.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, p2: Boolean) {
                blue = progress
                setDynamicUIElements()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })
        setDynamicUIElements()
        displayPalette()
    }

    private fun displayPalette() {
        val linearLayout: LinearLayout = findViewById(R.id.dialogPaletteColors)
        linearLayout.removeAllViews()

        for(i in 0 until palette.dbColorList.size) {
            val color = palette.dbColorList[i].color
            val imageButton = activity.createPaletteImageButton(color, paletteButtonAction)
            linearLayout.addView(imageButton)
        }

    }

    private val paletteButtonAction : (Long) -> Unit = {
        red = Color.red(Palette().colorInt(it))
        seekBarRed.progress = red
        green = Color.green(Palette().colorInt(it))
        seekBarGreen.progress = green
        blue = Color.blue(Palette().colorInt(it))
        seekBarBlue.progress = blue

        setDynamicUIElements()
    }
}