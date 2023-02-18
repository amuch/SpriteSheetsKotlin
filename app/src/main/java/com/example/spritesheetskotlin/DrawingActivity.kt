package com.example.spritesheetskotlin

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Point
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.example.spritesheetskotlin.bitmap.BitmapActionEnum
import com.example.spritesheetskotlin.bitmap.BitmapManager
import com.example.spritesheetskotlin.bitmap.BitmapViewModel
import com.example.spritesheetskotlin.dialog.*
import com.example.spritesheetskotlin.palette.*
import kotlin.math.max
import kotlin.system.exitProcess

class DrawingActivity : AppCompatActivity(), View.OnTouchListener {
    var spriteWidth = 20 as Int
    var spriteHeight = 20 as Int
    private val resolution: Int = 16
    lateinit var imageViewMain: ImageView
    lateinit var drawingViewModelFactory: DrawingViewModelFactory
    lateinit var drawingViewModel : DrawingViewModel
    lateinit var dialogViewModel: DialogViewModel
    var bitmapManager = BitmapManager()
    lateinit var bitmapViewModel: BitmapViewModel
    lateinit var dialogManager : DialogManager

    lateinit var palette: Palette
//    lateinit var colors: ArrayList<DBColor>
    private lateinit var linearLayoutPalette: LinearLayout

    private lateinit var imageButtonManagePalette: ImageButton
    private lateinit var imageButtonManageTools: ImageButton
    private lateinit var imageButtonBitmapActionDraw: ImageButton
    private lateinit var imageButtonBitmapActionFill: ImageButton
    private lateinit var imageButtonBitmapActionErase: ImageButton
    private lateinit var imageButtonBitmapActionClear: ImageButton
    private lateinit var imageButtonBitmapActionOverwrite: ImageButton
    private lateinit var imageButtonBitmapActionMirrorHorizontal: ImageButton
    private lateinit var imageButtonBitmapActionMirrorVertical: ImageButton

    companion object {
//        var blendArray = Array(spriteWidth) { BooleanArray(spriteHeight) }
        var mirrorHorizontal: Boolean = false
        var mirrorVertical: Boolean = false

        fun closeApplication() {
            exitProcess(0)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawing)

//        bindUI(intent.getStringExtra("nameProject").toString())
        bindUI()
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        super.onSaveInstanceState(bundle)

        if(dialogViewModel.dialogVisible.value == DialogVisibleEnum.DIALOG_NONE) {
            dialogManager.closeDialogs()
        }
    }

    override fun onRestoreInstanceState(bundle: Bundle) {
        super.onRestoreInstanceState(bundle)
        showDialog()
    }

    override fun onBackPressed() {
        dialogViewModel.dialogVisible.value = DialogVisibleEnum.DIALOG_CONFIRM_EXIT
        showDialog()
    }

    private fun showDialog() {
        dialogManager.showDialog(dialogViewModel)
    }

    /** Drawing methods **/
    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        when(view) {
            imageViewMain -> when(motionEvent?.action) {
                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_MOVE -> {
                    val point = bitmapManager.getStoragePixel(imageViewMain, motionEvent, bitmapManager.indexCurrent)
                    performDrawingAction(point)
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    view.performClick()
                    return true
                }
            }
        }
        return super.onTouchEvent(motionEvent)
    }

    private fun performDrawingAction(point: Point) {
        val width = drawingViewModel.bitmapStorage.value!!.width
        val horizontalMirror = Point(width - point.x - 1, point.y)

        val height = drawingViewModel.bitmapStorage.value!!.height
        val verticalMirror = Point(point.x, height - point.y - 1)

        val mirrorBoth = mirrorHorizontal && mirrorVertical
        val doubleMirror = Point(width - point.x - 1, height - point.y - 1)

        when (bitmapViewModel.bitmapAction.value) {
            BitmapActionEnum.BITMAP_OVERWRITE -> {
                colorPixel(point)

                if(mirrorHorizontal) {
                    colorPixel(horizontalMirror)
                }

                if(mirrorVertical) {
                    colorPixel(verticalMirror)
                }

                if(mirrorBoth) {
                    colorPixel(doubleMirror)
                }
            }

            BitmapActionEnum.BITMAP_BLEND -> {
//                bitmapManager.setHasBlended(point)
            }
            BitmapActionEnum.BITMAP_COLOR -> {
                if(bitmapManager.pointHasZeroAlpha(bitmapManager.indexCurrent, point)) {
                    colorPixel(point)

                    if(mirrorHorizontal) {
                        colorPixel(horizontalMirror)
                    }

                    if(mirrorVertical) {
                        colorPixel(verticalMirror)
                    }

                    if(mirrorBoth) {
                        colorPixel(doubleMirror)
                    }
                }
            }

            BitmapActionEnum.BITMAP_FILL -> {
                fillColorPartial(point)

                if(mirrorHorizontal) {
                    fillColorPartial(horizontalMirror)
                }

                if(mirrorVertical) {
                    fillColorPartial(verticalMirror)
                }

                if(mirrorBoth) {
                    fillColorPartial(doubleMirror)
                }
            }

            BitmapActionEnum.BITMAP_ERASE -> {
                erasePixel(point)

                if(mirrorHorizontal) {
                    erasePixel(horizontalMirror)
                }

                if(mirrorVertical) {
                    erasePixel(verticalMirror)
                }

                if(mirrorBoth) {
                    erasePixel(doubleMirror)
                }
            }

            null -> {}
        }
    }

    private fun colorPixel(point: Point) {
        val color = drawingViewModel.currentColor.value
        bitmapManager.colorPixel(bitmapManager.indexCurrent, point, color)
        bitmapManager.projectColor(drawingViewModel.bitmapMain.value, point, resolution, color)

        /**  Perhaps a coroutine here to ensure the operation has completed. **/
        setImageView(imageViewMain, drawingViewModel.bitmapMain.value)
    }

    private fun fillColorPartial(point: Point) {
        val color = drawingViewModel.currentColor.value
        bitmapManager.fillBitmapPartial(drawingViewModel.bitmapMain.value, bitmapManager.indexCurrent, point, resolution, color)
        setImageView(imageViewMain, drawingViewModel.bitmapMain.value)
    }

    private fun erasePixel(point: Point) {
        val color = COLOR_CLEAR
        bitmapManager.colorPixel(bitmapManager.indexCurrent, point, color)
        bitmapManager.projectColor(drawingViewModel.bitmapMain.value, point, resolution, color)
        setImageView(imageViewMain, drawingViewModel.bitmapMain.value)
    }

    private fun setImageView(imageView: ImageView, bitmap: Bitmap?) {
        imageView.setImageBitmap(bitmap)
    }

    /** Palette functionality **/
    private fun displayPalette() {
        linearLayoutPalette = findViewById(R.id.linearLayoutActivityDrawing)
        linearLayoutPalette.removeAllViews()
        val longest = max(resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
        val side = 30 * longest / (250)
        val margin = longest / (250)

        for(i in 0 until palette.dbColorList.size) {
            val imageButton = ImageButton(this).apply {
                setBackgroundResource(R.mipmap.background2)
                setImageDrawable(ColorDrawable(palette.dbColorList[i].color))
                layoutParams = colorImageParameters(side, side, margin)
                setOnClickListener{
                    val red = Color.red(palette.dbColorList[i].color)
                    val green = Color.green(palette.dbColorList[i].color)
                    val blue = Color.blue(palette.dbColorList[i].color)
                    val alpha = 0xFF

                    drawingViewModel.currentColor.value = Color.argb(alpha, red, green, blue)

                    if(BitmapActionEnum.BITMAP_ERASE == bitmapViewModel.bitmapAction.value) {
                        bitmapViewModel.bitmapAction.value = BitmapActionEnum.BITMAP_COLOR
                    }
                }
            }
            linearLayoutPalette.addView(imageButton)
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

    fun dialogManageTools(view: View) {
        println("View Tools")
    }

    fun dialogManagePalette(view: View) {
        dialogViewModel.dialogVisible.value = DialogVisibleEnum.DIALOG_MANAGE_PALETTE
        showDialog()
    }

    /** Frame functionality **/
    fun dialogManageFrame(view: View) {
        println("Manage Frame")
    }

    /** Bitmap functionality **/
    fun bitmapActionDraw(view: View) {
        bitmapViewModel.bitmapAction.value = BitmapActionEnum.BITMAP_COLOR
    }

    fun bitmapActionErase(view: View) {
        bitmapViewModel.bitmapAction.value = BitmapActionEnum.BITMAP_ERASE
    }

    fun bitmapActionOverwrite(view: View) {
        bitmapViewModel.bitmapAction.value = BitmapActionEnum.BITMAP_OVERWRITE
    }

    fun bitmapActionBlend(view: View) {
//        bitmapManager.initializeBlendArray()
//        bitmapViewModel.bitmapAction.value = BitmapActionEnum.BITMAP_BLEND
    }

    fun bitmapActionFill(view: View) {
        bitmapViewModel.bitmapAction.value = BitmapActionEnum.BITMAP_FILL
    }

    fun bitmapActionClear(view: View) {
        dialogViewModel.dialogVisible.value = DialogVisibleEnum.DIALOG_CONFIRM_CLEAR
        showDialog()
    }

    fun clearBitmap() {
        bitmapManager.fillBitmap(drawingViewModel.bitmapStorage.value, COLOR_CLEAR)
        bitmapManager.fillBitmap(drawingViewModel.bitmapMain.value, COLOR_CLEAR)
        setImageView(imageViewMain, drawingViewModel.bitmapMain.value)
    }

    fun bitmapActionMirrorHorizontal(view: View) {
        mirrorHorizontal = !mirrorHorizontal
        setButtonBackgrounds()
    }

    fun bitmapActionMirrorVertical(view: View) {
        mirrorVertical = !mirrorVertical
        setButtonBackgrounds()
    }

    private fun setButtonBackgrounds() {
        setButtonImage()
        setButtonColors()
    }

    private fun setButtonImage() {
//        val buttonBitmapActionBlend = findViewById<Button>(R.id.buttonBitmapActionBlend)
//        buttonBitmapActionBlend.setBackgroundColor(COLOR_BUTTON_INACTIVE)

        imageButtonBitmapActionDraw.setBackgroundColor(COLOR_CLEAR)
        imageButtonBitmapActionFill.setBackgroundColor(COLOR_CLEAR)
        imageButtonBitmapActionOverwrite.setBackgroundColor(COLOR_CLEAR)
        imageButtonBitmapActionErase.setBackgroundColor(COLOR_CLEAR)

        when(bitmapViewModel.bitmapAction.value) {
            BitmapActionEnum.BITMAP_FILL -> {
                drawingViewModel.currentColor.value?.let {
                    imageButtonBitmapActionFill.setBackgroundColor(it)
                    imageButtonManageTools.setBackgroundColor(it)
                }
                val bitMapFill: Bitmap = BitmapFactory.decodeResource(resources, R.mipmap.fill)
                imageButtonManageTools.setImageBitmap(bitMapFill)

            }

            BitmapActionEnum.BITMAP_BLEND ->{
//                buttonBitmapActionBlend.setBackgroundColor(COLOR_BUTTON_ACTIVE)
            }


            BitmapActionEnum.BITMAP_OVERWRITE -> {
                drawingViewModel.currentColor.value?.let {
                    imageButtonBitmapActionOverwrite.setBackgroundColor(it)
                    imageButtonManageTools.setBackgroundColor(it)
                }
                val bitmapOverwrite: Bitmap = BitmapFactory.decodeResource(resources, R.mipmap.overwrite)
                imageButtonManageTools.setImageBitmap(bitmapOverwrite)
            }

            BitmapActionEnum.BITMAP_ERASE -> {
                val bitmapEraser: Bitmap = BitmapFactory.decodeResource(resources, R.mipmap.erase)
                imageButtonManageTools.setBackgroundColor(COLOR_CLEAR)
                imageButtonManageTools.setImageBitmap(bitmapEraser)
            }

            null,
            BitmapActionEnum.BITMAP_COLOR -> {
                drawingViewModel.currentColor.value?.let {
                    imageButtonManageTools.setBackgroundColor(it)
                    imageButtonBitmapActionDraw.setBackgroundColor(it)
                }
                val bitmapDraw: Bitmap = BitmapFactory.decodeResource(resources, R.mipmap.draw)
                imageButtonManageTools.setImageBitmap(bitmapDraw)
            }
        }
    }

    private fun setButtonColors() {
        imageButtonBitmapActionMirrorHorizontal.setBackgroundColor(COLOR_CLEAR)
        if(mirrorHorizontal) {
            drawingViewModel.currentColor.value?.let {
                imageButtonBitmapActionMirrorHorizontal.setBackgroundColor(it)
            }
        }

        imageButtonBitmapActionMirrorVertical.setBackgroundColor(COLOR_CLEAR)
        if(mirrorVertical) {
            drawingViewModel.currentColor.value?.let {
                imageButtonBitmapActionMirrorVertical.setBackgroundColor(it)
            }
        }
    }

    /** File IO operation **/
    private val activityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        isGranted ->
            if(isGranted) {
                dialogViewModel.dialogVisible.value = DialogVisibleEnum.DIALOG_SAVE_BITMAP
                showDialog()
            }
            else {
                Toast.makeText(applicationContext, R.string.write_external_storage_denied, Toast.LENGTH_LONG).show()
            }
    }
    fun dialogSaveBitmap(view: View) {
        activityResultLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    /** Helper methods **/
    @SuppressLint("ClickableViewAccessibility")
    private fun bindUI(projectName: String = "Pixel Fart") {
        imageViewMain = findViewById(R.id.imageViewMain)!!
        imageViewMain.setOnTouchListener(this)

        drawingViewModelFactory = DrawingViewModelFactory(spriteWidth, spriteHeight, resolution, Color.BLACK)
        drawingViewModel = ViewModelProvider(this, drawingViewModelFactory).get(DrawingViewModel::class.java)

        drawingViewModel.bitmapMain.observe(this) {
            setImageView(imageViewMain, it)
        }

        drawingViewModel.bitmapStorage.observe(this) { }

        bitmapViewModel = ViewModelProvider(this).get(BitmapViewModel::class.java)
        bitmapViewModel.bitmapAction.observe(this) {
            setButtonBackgrounds()
        }
        if(null == bitmapViewModel.bitmapAction.value) {
            bitmapViewModel.bitmapAction.value = BitmapActionEnum.BITMAP_COLOR
        }

        initializeToolButtons()


        drawingViewModel.currentColor.observe(this) {
            imageButtonManageTools.setBackgroundColor(it)
//            imageButtonManagePalette.setBackgroundColor(it)
            setButtonBackgrounds()
//            Palette().colorImageButton(it, imageButtonManagePalette, resources)
        }

        drawingViewModel.bitmapStorage.value?.let {
            bitmapManager.bitmapList.add(it)
//            bitmapManager.initializeBlendArray()
        }

        val test: TextView = findViewById(R.id.textViewProjectTitle)
        test.text = projectName

        palette = Palette()
        palette.createTestPalette()
        displayPalette()

        dialogManager = drawingViewModel.bitmapMain.value?.let { DialogManager(this, it, palette) }!!

        dialogViewModel = ViewModelProvider(this).get(DialogViewModel::class.java)
        dialogViewModel.dialogVisible.observe(this) {}
    }

    private fun initializeToolButtons() {
        imageButtonManagePalette = findViewById(R.id.buttonManagePalette)

        imageButtonManageTools = findViewById(R.id.imageButtonManageTools)
        imageButtonBitmapActionDraw = findViewById(R.id.imageButtonBitmapActionDraw)
        imageButtonBitmapActionErase = findViewById(R.id.imageButtonBitmapActionErase)
        imageButtonBitmapActionFill = findViewById(R.id.imageButtonBitmapActionFill)
        imageButtonBitmapActionClear = findViewById(R.id.imageButtonBitmapActionClear)
        imageButtonBitmapActionOverwrite = findViewById(R.id.imageButtonBitmapActionOverwrite)
        imageButtonBitmapActionMirrorHorizontal = findViewById(R.id.imageButtonBitmapActionMirrorHorizontal)
        imageButtonBitmapActionMirrorVertical = findViewById(R.id.imageButtonBitmapActionMirrorVertical)
    }
}