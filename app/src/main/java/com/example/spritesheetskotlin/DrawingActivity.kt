package com.example.spritesheetskotlin

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.core.graphics.toColorInt
import androidx.lifecycle.ViewModelProvider
import com.example.spritesheetskotlin.bitmap.BitmapActionEnum
import com.example.spritesheetskotlin.bitmap.BitmapManager
import com.example.spritesheetskotlin.bitmap.BitmapViewModel
import com.example.spritesheetskotlin.database.Database
import com.example.spritesheetskotlin.database.PALETTE_DEFAULT
import com.example.spritesheetskotlin.dialog.*
import com.example.spritesheetskotlin.palette.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.system.exitProcess

class DrawingActivity : AppCompatActivity(), View.OnTouchListener {
//    lateinit var database: Database
    private val coroutineScopeMain = CoroutineScope(Dispatchers.Main)
    var spriteWidth = 20 as Int
    var spriteHeight = 20 as Int
    private val resolution: Int = 16
    lateinit var imageViewMain: ImageView
    lateinit var drawingViewModelFactory: DrawingViewModelFactory
    lateinit var drawingViewModel : DrawingViewModel
    lateinit var dialogViewModel: DialogViewModel
    lateinit var paletteViewModel: PaletteViewModel
    lateinit var paletteViewModelFactory: PaletteViewModelFactory
    var bitmapManager = BitmapManager()
    lateinit var bitmapViewModel: BitmapViewModel
    lateinit var dialogManager : DialogManager

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

    private lateinit var imageButtonManageFrame: ImageButton

    companion object {
        var iter = 0
//        var blendArray = Array(spriteWidth) { BooleanArray(spriteHeight) }
        var mirrorHorizontal: Boolean = false
        var mirrorVertical: Boolean = false

        fun closeApplication() {
            exitProcess(0)
        }
        lateinit var namePalette: String
        lateinit var palette: Palette

        lateinit var database : Database

//        fun applicationContext() : Context {
//            return DrawingActivity.applicationContext()
//        }
//
//        fun showToast(messageToast: String) {
//
//            Toast.makeText(applicationContext(), messageToast, Toast.LENGTH_LONG).show()
//        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawing)
        database = Database(this)

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
        val color = paletteViewModel.currentColor.value
        bitmapManager.colorPixel(bitmapManager.indexCurrent, point, color)
        imageButtonManageFrame.setImageBitmap(drawingViewModel.bitmapStorage.value)

        bitmapManager.projectColor(drawingViewModel.bitmapMain.value, point, resolution, color)
        setImageView(imageViewMain, drawingViewModel.bitmapMain.value)
    }

    private fun fillColorPartial(point: Point) {
        val color = paletteViewModel.currentColor.value
        bitmapManager.fillBitmapPartial(drawingViewModel.bitmapMain.value, bitmapManager.indexCurrent, point, resolution, color)
        imageButtonManageFrame.setImageBitmap(drawingViewModel.bitmapStorage.value)
        setImageView(imageViewMain, drawingViewModel.bitmapMain.value)
    }

    private fun erasePixel(point: Point) {
        val color = COLOR_CLEAR
        bitmapManager.colorPixel(bitmapManager.indexCurrent, point, color)
        imageButtonManageFrame.setImageBitmap(drawingViewModel.bitmapStorage.value)

        bitmapManager.projectColor(drawingViewModel.bitmapMain.value, point, resolution, color)
        setImageView(imageViewMain, drawingViewModel.bitmapMain.value)
    }

    private fun setImageView(imageView: ImageView, bitmap: Bitmap?) {
        imageView.setImageBitmap(bitmap)
    }

    /** Palette functionality **/
    fun displayPalette() {
        println("Display Palette")
        linearLayoutPalette = findViewById(R.id.linearLayoutActivityDrawing)
        linearLayoutPalette.removeAllViews()

        for(i in 0 until paletteViewModel.palette.value!!.dbColorList.size) {
            val color = paletteViewModel.palette.value!!.dbColorList[i].color
            linearLayoutPalette.addView(createPaletteImageButton(color, paletteButtonAction))
        }
    }

    fun createPaletteImageButton(color: Long, clickMethod: (color: Long) -> Unit) : ImageButton {
        val longest = max(resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
        val side = 30 * longest / (250)
        val margin = longest / (250)

        val imageButton = ImageButton(this).apply {
            setBackgroundResource(R.mipmap.background2)
            setImageDrawable(ColorDrawable(Palette().colorInt(color)))
            layoutParams = colorImageParameters(side, side, 0)
            setOnClickListener {
                clickMethod(color)
            }
        }
        return imageButton
    }

    private val paletteButtonAction : (Long) -> Unit = {
        if(BitmapActionEnum.BITMAP_ERASE == bitmapViewModel.bitmapAction.value) {
            bitmapViewModel.bitmapAction.value = BitmapActionEnum.BITMAP_COLOR
        }

        if(BitmapActionEnum.BITMAP_FILL == bitmapViewModel.bitmapAction.value) {
            if(paletteViewModel.currentColor.value != it) {
                bitmapViewModel.bitmapAction.value = BitmapActionEnum.BITMAP_COLOR
            }
        }

        paletteViewModel.currentColor.value = it
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
//            Toast.makeText(this, database.deletePalette(name), Toast.LENGTH_LONG).show()
        val dbPalette = database.readPalette(namePalette)
        println("Db palette: $dbPalette")
//        if(null != dbPalette) {
            coroutineScopeMain.launch {
                database.createDefaultPalettes()
            }
//        }
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
        imageButtonManageFrame.setImageBitmap(drawingViewModel.bitmapStorage.value)

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

        imageButtonBitmapActionDraw.setBackgroundColor(Palette().colorInt(COLOR_CLEAR))
        imageButtonBitmapActionFill.setBackgroundColor(Palette().colorInt(COLOR_CLEAR))
        imageButtonBitmapActionOverwrite.setBackgroundColor(Palette().colorInt(COLOR_CLEAR))
        imageButtonBitmapActionErase.setBackgroundColor(Palette().colorInt(COLOR_CLEAR))

        when(bitmapViewModel.bitmapAction.value) {
            BitmapActionEnum.BITMAP_FILL -> {
                paletteViewModel.currentColor.value?.let {
                    imageButtonBitmapActionFill.setBackgroundColor(Palette().colorInt(it))
                    imageButtonManageTools.setBackgroundColor(Palette().colorInt(it))
                }
                val bitMapFill: Bitmap = BitmapFactory.decodeResource(resources, R.mipmap.bucket)
                imageButtonManageTools.setImageBitmap(bitMapFill)

            }

            BitmapActionEnum.BITMAP_BLEND -> {
//                buttonBitmapActionBlend.setBackgroundColor(COLOR_BUTTON_ACTIVE)
            }


            BitmapActionEnum.BITMAP_OVERWRITE -> {
                paletteViewModel.currentColor.value?.let {
                    imageButtonBitmapActionOverwrite.setBackgroundColor(Palette().colorInt(it))
                    imageButtonManageTools.setBackgroundColor(Palette().colorInt(it))
                }
                val bitmapOverwrite: Bitmap = BitmapFactory.decodeResource(resources, R.mipmap.overwrite)
                imageButtonManageTools.setImageBitmap(bitmapOverwrite)
            }

            BitmapActionEnum.BITMAP_ERASE -> {
                val bitmapEraser: Bitmap = BitmapFactory.decodeResource(resources, R.mipmap.erase)
                imageButtonManageTools.setBackgroundColor(Palette().colorInt(COLOR_CLEAR))
                imageButtonManageTools.setImageBitmap(bitmapEraser)
            }

            null,
            BitmapActionEnum.BITMAP_COLOR -> {
                paletteViewModel.currentColor.value?.let {
                    imageButtonManageTools.setBackgroundColor(Palette().colorInt(it))
                    imageButtonBitmapActionDraw.setBackgroundColor(Palette().colorInt(it))
                }
                val bitmapDraw: Bitmap = BitmapFactory.decodeResource(resources, R.mipmap.draw)
                imageButtonManageTools.setImageBitmap(bitmapDraw)
            }
        }
    }

    private fun setButtonColors() {
        imageButtonBitmapActionMirrorHorizontal.setBackgroundColor(Palette().colorInt(COLOR_CLEAR))
        imageButtonBitmapActionMirrorVertical.setBackgroundColor(Palette().colorInt(COLOR_CLEAR))

        if(BitmapActionEnum.BITMAP_ERASE != bitmapViewModel.bitmapAction.value) {
            if(mirrorHorizontal) {
                paletteViewModel.currentColor.value?.let {

                    imageButtonBitmapActionMirrorHorizontal.setBackgroundColor(Palette().colorInt(it))
                }
            }
            if(mirrorVertical) {
                paletteViewModel.currentColor.value?.let {
                    imageButtonBitmapActionMirrorVertical.setBackgroundColor(Palette().colorInt(it))
                }
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
        initializeUIImages()
        initializeViewModels()
        initializeButtons()
        initializePalette()
        initializeDialogManager()

        val test: TextView = findViewById(R.id.textViewProjectTitle)
        test.text = projectName
    }

    private fun initializeUIImages() {
        imageViewMain = findViewById(R.id.imageViewMain)!!
        imageViewMain.setOnTouchListener(this)
    }

    private fun initializeViewModels() {
        initializeDrawingViewModel()
        initializeBitmapViewModel()
    }

    private fun initializeDrawingViewModel() {
        drawingViewModelFactory = DrawingViewModelFactory(spriteWidth, spriteHeight, resolution)
        drawingViewModel = ViewModelProvider(this, drawingViewModelFactory).get(DrawingViewModel::class.java)
        drawingViewModel.bitmapMain.observe(this) {
            setImageView(imageViewMain, it)
        }
        drawingViewModel.bitmapStorage.observe(this) {
            imageButtonManageFrame.setImageBitmap(it)
        }

        drawingViewModel.bitmapStorage.value?.let {
            bitmapManager.bitmapList.add(it)
//            bitmapManager.initializeBlendArray()
        }
    }

    private fun initializeBitmapViewModel() {
        bitmapViewModel = ViewModelProvider(this).get(BitmapViewModel::class.java)
        bitmapViewModel.bitmapAction.observe(this) {
            setButtonBackgrounds()
        }
        if(null == bitmapViewModel.bitmapAction.value) {
            bitmapViewModel.bitmapAction.value = BitmapActionEnum.BITMAP_COLOR
        }
    }

    private fun initializeButtons() {
        initializePaletteButton()
        initializeToolButtons()
        initializeFrame()
    }

    private fun initializePaletteButton() {
        imageButtonManagePalette = findViewById(R.id.buttonManagePalette)
    }

    private fun initializeToolButtons() {
        imageButtonManageTools = findViewById(R.id.imageButtonManageTools)
        imageButtonBitmapActionDraw = findViewById(R.id.imageButtonBitmapActionDraw)
        imageButtonBitmapActionErase = findViewById(R.id.imageButtonBitmapActionErase)
        imageButtonBitmapActionFill = findViewById(R.id.imageButtonBitmapActionFill)
        imageButtonBitmapActionClear = findViewById(R.id.imageButtonBitmapActionClear)
        imageButtonBitmapActionOverwrite = findViewById(R.id.imageButtonBitmapActionOverwrite)
        imageButtonBitmapActionMirrorHorizontal = findViewById(R.id.imageButtonBitmapActionMirrorHorizontal)
        imageButtonBitmapActionMirrorVertical = findViewById(R.id.imageButtonBitmapActionMirrorVertical)
    }

    private fun initializeFrame() {
        imageButtonManageFrame = findViewById(R.id.imageButtonManageFrame)
    }

    private fun initializePalette() {
        namePalette = PALETTE_DEFAULT
        val dbPalette = database.readPalette(namePalette)

        palette = Palette()
        palette.initializePalette(database, dbPalette.id)

        paletteViewModelFactory = PaletteViewModelFactory(palette)
        paletteViewModel = ViewModelProvider(this, paletteViewModelFactory).get(PaletteViewModel::class.java)

        paletteViewModel.palette.observe(this) {
            if(it.dbColorList.size == 0) {
//                val dbPalette = database.readPalette(namePalette)
//                it.initializePalette(database, dbPalette.id)
//                displayPalette()
            }
        }

        paletteViewModel.currentColor.observe(this) {
            imageButtonManageTools.setBackgroundColor(Palette().colorInt(it))
            setButtonBackgrounds()
        }

        displayPalette()
    }

    private fun initializeDialogManager() {
        dialogManager = drawingViewModel.bitmapMain.value?.let {
            DialogManager(this, it, paletteViewModel.palette.value!!)
        }!!

        dialogViewModel = ViewModelProvider(this).get(DialogViewModel::class.java)
        dialogViewModel.dialogVisible.observe(this) {}
    }
}