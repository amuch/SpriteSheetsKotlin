package com.example.spritesheetskotlin.dialog

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.Window
import android.widget.*
import com.example.spritesheetskotlin.DrawingActivity
import com.example.spritesheetskotlin.R
import com.example.spritesheetskotlin.palette.Palette
import com.example.spritesheetskotlin.palette.PaletteViewModel

class DialogManagePalette(activity: DrawingActivity, paletteViewModel: PaletteViewModel): Dialog(activity) {
    private var paletteViewModel: PaletteViewModel
    private var activity: DrawingActivity
    private lateinit var imageButtonClose: ImageButton
    private lateinit var linearLayoutAdd: LinearLayout
    private lateinit var imageViewAdd: ImageView
    private lateinit var linearLayoutUpdate: LinearLayout
    private lateinit var imageViewUpdateBegin : ImageView
    private lateinit var imageViewUpdateEnd: ImageView
    private lateinit var linearLayoutDelete: LinearLayout
    private lateinit var imageViewDelete: ImageView
    private lateinit var textViewRGB: TextView
    private lateinit var textViewHex: TextView
    private lateinit var seekBarRed: SeekBar
    private lateinit var seekBarGreen: SeekBar
    private lateinit var seekBarBlue: SeekBar
    private var red: Int = 0
    private var green: Int = 0
    private var blue: Int = 0
    private var name: String = ""
    private var colorToUpdate: Long? = null

    init {
        setCancelable(false)
        paletteViewModel.also {
            this.paletteViewModel = it
            this.colorToUpdate = paletteViewModel.currentColor.value
            this.red = Palette().colorInt(this.colorToUpdate!!)
            this.green = Palette().colorInt(this.colorToUpdate!!)
            this.blue = Palette().colorInt(this.colorToUpdate!!)
        }
        activity.also { this.activity = it }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_manage_palette)

        bindUI()
    }

    override fun show() {
        super.show()
        this.colorToUpdate = paletteViewModel.currentColor.value

        this.red = this.colorToUpdate?.shr(16)?.and(0xFF)!!.toInt()
        this.green = this.colorToUpdate?.shr(8)?.and(0xFF)!!.toInt()
        this.blue = this.colorToUpdate?.and(0xFF)!!.toInt()
        setDynamicUIElements()
    }
    private fun textFromComponents(redComponent : Int, greenComponent: Int, blueComponent:Int) : Int {
        if((redComponent + greenComponent + blueComponent) < (127 * 3)) {
            return Color.WHITE
        }
        return Color.BLACK
    }

    private fun setButtonAcceptColor() {
        imageViewAdd.setBackgroundColor(Color.argb(0xFF, red, green, blue))
        imageViewUpdateEnd.setBackgroundColor(Color.argb(0xFF, red, green, blue))
    }

    private fun setUpdateButtonColor() {
        imageViewUpdateBegin.setBackgroundColor(colorToUpdate!!.toInt())
    }

    private fun setDeleteButtonColor() {
        imageViewDelete.setBackgroundColor(colorToUpdate!!.toInt())
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
        if(null != this.colorToUpdate) {
            setUpdateButtonColor()
            setDeleteButtonColor()
        }
        setTextViewRGB()
        setTextViewHex()
        setSeekBarProgress()
    }

    private fun bindUI() {
        textViewRGB = findViewById<TextView>(R.id.textViewDialogColorRGB)
        textViewHex = findViewById<TextView>(R.id.textViewDialogColorHex)

        imageViewAdd = findViewById(R.id.dialogManagePaletteImageViewAdd)
        imageViewUpdateBegin = findViewById(R.id.dialogManagePaletteImageViewUpdateBegin)
        imageViewUpdateEnd = findViewById(R.id.dialogManagePaletteImageViewUpdateEnd)
        imageViewDelete = findViewById(R.id.dialogManagePaletteImageViewDelete)

        imageButtonClose = findViewById(R.id.dialogManagePaletteImageButtonClose)
        imageButtonClose.setOnClickListener {
            this.cancel()
        }

        linearLayoutUpdate = findViewById<LinearLayout>(R.id.dialogManagePaletteLinearLayoutUpdate)
        linearLayoutUpdate.setOnClickListener {
            if(null != this.colorToUpdate) {
                val dbPalette = DrawingActivity.database.readPalette(DrawingActivity.namePalette)

                val color = Color.argb(0xFF, red, green, blue).toLong()
                DrawingActivity.database.updateColor(dbPalette.id, colorToUpdate!!, color)
                paletteViewModel.palette.value!!.updateColor(this.colorToUpdate!!, color)

                paletteViewModel.currentColor.value = color
                activity.setButtonImage()
                activity.displayPalette()
                displayPalette()
            }

            this.cancel()
        }

        linearLayoutAdd = findViewById(R.id.dialogManagePaletteLinearLayoutAdd)
        linearLayoutAdd.setOnClickListener {

            val dbPalette = DrawingActivity.database.readPalette(DrawingActivity.namePalette)

            val color = Color.argb(0xFF, red, green, blue).toLong()
            if(!DrawingActivity.database.colorIsInPalette(color, dbPalette.id)) {
                DrawingActivity.database.createColor(name, color, dbPalette.id)
                paletteViewModel.palette.value!!.addColor(color)

                paletteViewModel.currentColor.value = color
                activity.setButtonImage()
                activity.displayPalette()
                displayPalette()
            }

            this.cancel()
        }

        linearLayoutDelete = findViewById(R.id.dialogManagePaletteLinearLayoutDelete)
        linearLayoutDelete.setOnClickListener {
            if(null != this.colorToUpdate) {
                if(paletteViewModel.palette.value!!.dbColorList.size > 0) {
                    val dbPalette = DrawingActivity.database.readPalette(DrawingActivity.namePalette)
                    DrawingActivity.database.deleteColor(dbPalette.id, colorToUpdate!!)
                    paletteViewModel.palette.value!!.deleteColor(this.colorToUpdate!!)
                    if(this.colorToUpdate!! == paletteViewModel.currentColor.value) {
                        paletteViewModel.currentColor.value = paletteViewModel.palette.value!!.getColorByIndex(0)
                    }
                    activity.displayPalette()
                    displayPalette()
                }
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

        for(i in 0 until paletteViewModel.palette.value!!.dbColorList.size) {
            val color = paletteViewModel.palette.value!!.dbColorList[i].color
            val imageButton = activity.createPaletteImageButton(color, paletteButtonAction)
            linearLayout.addView(imageButton)
        }

    }

    private val paletteButtonAction : (Long) -> Unit = {
        red = Color.red(Palette().colorInt(it))
        green = Color.green(Palette().colorInt(it))
        blue = Color.blue(Palette().colorInt(it))

        colorToUpdate = it
        paletteViewModel.currentColor.value = it
        activity.setButtonImage()
        setDynamicUIElements()
    }

    private fun setSeekBarProgress() {
        seekBarRed.progress = red ?: 0
        seekBarGreen.progress = green ?: 0
        seekBarBlue.progress = blue ?: 0
    }
}