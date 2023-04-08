package com.example.spritesheetskotlin

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.*
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.system.exitProcess

const val IMAGE_BUTTON_FRAME_BASE = 1000
const val SPRITE_DIMENSION_DEFAULT = 32
const val SPRITE_RESOLUTION_DEFAULT = 8
const val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 100

class DrawingActivity : AppCompatActivity(), View.OnTouchListener {
    private val coroutineScopeMain = CoroutineScope(Dispatchers.Main)

    var spriteWidth = SPRITE_DIMENSION_DEFAULT as Int
    var spriteHeight = SPRITE_DIMENSION_DEFAULT as Int
    var resolution: Int = SPRITE_RESOLUTION_DEFAULT

    lateinit var imageViewMain: ImageView
    lateinit var drawingViewModelFactory: DrawingViewModelFactory
    lateinit var drawingViewModel : DrawingViewModel
    lateinit var dialogViewModel: DialogViewModel
    lateinit var paletteViewModel: PaletteViewModel
    lateinit var paletteViewModelFactory: PaletteViewModelFactory
    lateinit var bitmapViewModel: BitmapViewModel
    lateinit var dialogManager : DialogManager

    private lateinit var linearLayoutPalette: LinearLayout
    private lateinit var linearLayoutFrame: LinearLayout

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
        var mirrorHorizontal: Boolean = false
        var mirrorVertical: Boolean = false

        fun closeApplication() {
            exitProcess(0)
        }
        lateinit var namePalette: String
        lateinit var palette: Palette

        lateinit var database : Database
    }

    init {
        database = Database(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawing)

        resolution = intent.getIntExtra(PREFERENCE_RESOLUTION, SPRITE_RESOLUTION_DEFAULT)
        spriteWidth = intent.getIntExtra(PREFERENCE_DIMENSION, SPRITE_DIMENSION_DEFAULT)
        spriteHeight = intent.getIntExtra(PREFERENCE_DIMENSION, SPRITE_DIMENSION_DEFAULT)

//        bindUI(intent.getStringExtra("nameProject").toString())
        bindUI()
        showDialog()
    }

    override fun onDestroy() {
        dialogManager.closeDialogs()
        super.onDestroy()
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
        dialogManager.showDialog()
    }

    /** Drawing methods **/
    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        when(view) {
            imageViewMain -> when(motionEvent?.action) {
                MotionEvent.ACTION_DOWN,
                MotionEvent.ACTION_MOVE -> {
                    val point = bitmapManager().getStoragePixel(imageViewMain, motionEvent, indexCurrent())
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
        coroutineScopeMain.launch {
            val points = ArrayList<Point>()
            points.add(point)

            val width = storageBitmap().width
            if (mirrorHorizontal) {
                val horizontalMirror = Point(width - point.x - 1, point.y)
                points.add(horizontalMirror)
            }

            val height = storageBitmap().height
            if (mirrorVertical) {
                val verticalMirror = Point(point.x, height - point.y - 1)
                points.add(verticalMirror)
            }

            if (mirrorHorizontal && mirrorVertical) {
                val doubleMirror = Point(width - point.x - 1, height - point.y - 1)
                points.add(doubleMirror)
            }

            when (bitmapViewModel.bitmapAction.value) {
                BitmapActionEnum.BITMAP_OVERWRITE -> {
                    for (i in 0 until points.size) {
                        colorPixel(points[i])
                    }
                }

                BitmapActionEnum.BITMAP_BLEND -> {
                    //                bitmapManager.setHasBlended(point)
                }

                BitmapActionEnum.BITMAP_COLOR -> {
                    for (i in 0 until points.size) {
                        if (bitmapManager().pointHasZeroAlpha(indexCurrent(), points[i])) {
                            colorPixel(points[i])
                        }
                    }
                }

                BitmapActionEnum.BITMAP_FILL -> {
                    for (i in 0 until points.size) {
                        fillColorPartial(points[i])
                    }
                }

                BitmapActionEnum.BITMAP_ERASE -> {
                    for (i in 0 until points.size) {
                        erasePixel(points[i])
                    }
                }

                null -> {}
            }
        }
    }

    private fun colorPixel(point: Point) {
        val color = paletteViewModel.currentColor.value
        bitmapManager().colorPixel(storageBitmap(), point, color)
        updateFrameImageButton(storageBitmap())

        bitmapManager().projectColor(drawingViewModel.bitmapMain.value, point, resolution, color)
        setImageView(imageViewMain, drawingViewModel.bitmapMain.value)
    }

    private fun fillColorPartial(point: Point) {
        val color = paletteViewModel.currentColor.value
        bitmapManager().fillBitmapPartial(drawingViewModel.bitmapMain.value, indexCurrent(), point, resolution, color)
        updateFrameImageButton(storageBitmap())
        setImageView(imageViewMain, drawingViewModel.bitmapMain.value)
    }

    private fun erasePixel(point: Point) {
        val color = COLOR_CLEAR
        bitmapManager().colorPixel(indexCurrent(), point, color)
        updateFrameImageButton(storageBitmap())

        bitmapManager().projectColor(drawingViewModel.bitmapMain.value, point, resolution, color)
        setImageView(imageViewMain, drawingViewModel.bitmapMain.value)
    }

    private fun setImageView(imageView: ImageView, bitmap: Bitmap?) {
        imageView.setImageBitmap(bitmap)
    }

    /** Palette functionality **/
    fun displayPalette() {
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
    fun displayFrames() {
        linearLayoutFrame = findViewById(R.id.linearLayoutFrame)
        linearLayoutFrame.removeAllViews()

        for(i in 0 until bitmapManager().bitmapList.size) {
            linearLayoutFrame.addView(createFrameImageButton(i, bitmapManager().bitmapList[i], IMAGE_BUTTON_FRAME_BASE))
        }
    }

    fun createFrameImageButton(index: Int, bitmap: Bitmap?, idBase: Int) : ImageButton {
        val longest = max(resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
        val side = 30 * longest / (250)
        val margin = longest / (250)

        val imageButton = ImageButton(this).apply {
            layoutParams = frameImageParameters(side, side, margin)
            setImageBitmap(bitmap)
            setBackgroundResource(R.mipmap.background16)
            scaleType = ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
            setPadding(0, 0, 0,0)
            id = idBase + index

            setOnClickListener {
                setStorageBitmap(index)
//                dialogManager.updateBitmapMain(drawingViewModel.bitmapMain.value!!)
            }
        }

        return imageButton
    }

    fun frameImageParameters(width: Int, height: Int, margin: Int) : LinearLayout.LayoutParams {
        val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.CENTER
//        val orientation = resources.configuration.orientation
//        if(Configuration.ORIENTATION_PORTRAIT == orientation) {
//
//        }
        params.setMargins(margin, margin, margin, margin)
        params.width = width
        params.height = height

        return params
    }

    private val frameButtonAction : (Int) -> Unit = {
        bitmapManager().indexCurrent = it
    }

    fun updateFrameImageButton(bitmap: Bitmap?) {
        val imageButton = findViewById<ImageButton>(IMAGE_BUTTON_FRAME_BASE + indexCurrent())
        imageButton.setImageBitmap(bitmap)
    }

    fun dialogManageFrame(view: View) {
        dialogViewModel.dialogVisible.value = DialogVisibleEnum.DIALOG_MANAGE_FRAME
        showDialog()
    }

    /** Bitmap functionality **/
    fun setStorageBitmap(index: Int) {
        drawingViewModel.bitmapManager.value!!.indexCurrent = index
        drawingViewModel.bitmapMain.value = bitmapManager().projectBitmap(index, resolution)
    }

    fun bitmapMain() : Bitmap {
        return drawingViewModel.bitmapMain.value!!
    }

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
        val bitmap = storageBitmap()
        bitmapManager().fillBitmap(bitmap, COLOR_CLEAR)
        updateFrameImageButton(bitmap)

        bitmapManager().fillBitmap(drawingViewModel.bitmapMain.value, COLOR_CLEAR)
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

    private fun indexCurrent() : Int {
        return drawingViewModel.bitmapManager.value!!.indexCurrent
    }

    private fun storageBitmap() : Bitmap {
        return bitmapManager().bitmapList[indexCurrent()]
    }

    private fun bitmapManager() : BitmapManager {
        return drawingViewModel.bitmapManager.value!!
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
                val bitMapFill: Bitmap = BitmapFactory.decodeResource(resources, R.mipmap.fill32)
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
                val bitmapOverwrite: Bitmap = BitmapFactory.decodeResource(resources, R.mipmap.overwrite32)
                imageButtonManageTools.setImageBitmap(bitmapOverwrite)
            }

            BitmapActionEnum.BITMAP_ERASE -> {
                val bitmapEraser: Bitmap = BitmapFactory.decodeResource(resources, R.mipmap.erase32)
                imageButtonManageTools.setBackgroundColor(Palette().colorInt(COLOR_CLEAR))
                imageButtonManageTools.setImageBitmap(bitmapEraser)
            }

            BitmapActionEnum.BITMAP_COLOR,
            null -> {
                paletteViewModel.currentColor.value?.let {
                    imageButtonManageTools.setBackgroundColor(Palette().colorInt(it))
                    imageButtonBitmapActionDraw.setBackgroundColor(Palette().colorInt(it))
                }
                val bitmapDraw: Bitmap = BitmapFactory.decodeResource(resources, R.mipmap.draw32)
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

    /** Settings operation **/
    private fun checkPermissions(permission: String, name: String, requestCode: Int) {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M - 1) {
            when {
                ContextCompat.checkSelfPermission(applicationContext, permission) == PackageManager.PERMISSION_GRANTED -> {
                    println("Permission Accepted")
                }
                shouldShowRequestPermissionRationale(permission) -> {
                    println("Permission show dialog")
                    showPermissionDialog(permission, name, requestCode)
                }
                else -> {
                    println("Permission request")
                    ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
                }
            }
        }

    }

    private fun showPermissionDialog(permission: String, name: String, requestCode: Int) {
        val builder = AlertDialog.Builder(this)

        builder.apply {
            setMessage("Permission to access $name is required to save an image")
            setTitle("Permission Required")
            setPositiveButton("OK") {dialog, which ->
                ActivityCompat.requestPermissions(this@DrawingActivity, arrayOf(permission), requestCode)
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        fun innerCheck(name: String) {
            if(grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, R.string.write_external_storage_denied, Toast.LENGTH_LONG)
            }
//            else {
//                Toast.makeText(applicationContext, "Accepted", Toast.LENGTH_LONG)
//            }
        }

        when (requestCode) {
            REQUEST_CODE_WRITE_EXTERNAL_STORAGE -> innerCheck("External Storage")
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

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
        println("Save bitmap clicked.")
//        checkPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, "External Storage", REQUEST_CODE_WRITE_EXTERNAL_STORAGE)
        activityResultLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    fun dialogSettings(view: View) {
        dialogViewModel.dialogVisible.value = DialogVisibleEnum.DIALOG_SETTINGS
        showDialog()
    }

    /** Helper methods **/
    @SuppressLint("ClickableViewAccessibility")
    private fun bindUI(projectName: String = "Pixel Art") {
        initializeUIImages()
        initializeViewModels()
        initializeButtons()
        initializePalette()
        initializeDialogManager()

        displayFrames()
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
        drawingViewModel.bitmapManager.observe(this) {
            if(0 == it.bitmapList.size) {
                it.bitmapList.add(Bitmap.createBitmap(spriteWidth, spriteHeight, Bitmap.Config.ARGB_8888))
                displayFrames()
            }
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
        palette.initializePalette(dbPalette.id)

        paletteViewModelFactory = PaletteViewModelFactory(palette)
        paletteViewModel = ViewModelProvider(this, paletteViewModelFactory).get(PaletteViewModel::class.java)

        paletteViewModel.palette.observe(this) { }

        paletteViewModel.currentColor.observe(this) {
            imageButtonManageTools.setBackgroundColor(Palette().colorInt(it))
            setButtonBackgrounds()
        }

        displayPalette()
    }

    private fun initializeDialogManager() {
        dialogViewModel = ViewModelProvider(this).get(DialogViewModel::class.java)
        dialogViewModel.dialogVisible.observe(this) {}

        dialogManager = drawingViewModel.bitmapMain.value?.let {
            DialogManager(this, drawingViewModel, paletteViewModel, dialogViewModel, bitmapManager(), database)
        }!!
    }
}