package com.example.spritesheetskotlin.dialog

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Window
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.spritesheetskotlin.DrawingActivity
import com.example.spritesheetskotlin.DrawingViewModel
import com.example.spritesheetskotlin.R
import com.example.spritesheetskotlin.bitmap.BitmapManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.concurrent.thread
import kotlin.math.max

const val IMAGE_BUTTON_DIALOG_FRAME_BASE = 2000

class DialogManageFrame(activity: DrawingActivity, bitmapManager: BitmapManager, drawingViewModel: DrawingViewModel): Dialog(activity)  {
    private var activity: DrawingActivity
    private var bitmapManager: BitmapManager
    private var drawingViewModel: DrawingViewModel
    private lateinit var buttonClose: Button
    private lateinit var buttonAdd: Button
    private lateinit var buttonDuplicate: Button
    private lateinit var buttonDelete: Button
    private lateinit var imageViewMainFrame: ImageView
//    private lateinit var buttonAccept: Button

    init {
        setCancelable(false)
        activity.also { this.activity = it }
        bitmapManager.also { this.bitmapManager = it}
        drawingViewModel.also{ this.drawingViewModel = it}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_manage_frame)

        bindUI()
    }

    override fun show() {
        super.show()
        updateBitmap()
    }

    fun updateBitmap() {
        imageViewMainFrame.setImageBitmap(this.drawingViewModel.bitmapMain.value)
    }

    private fun bindUI() {
        imageViewMainFrame = findViewById(R.id.imageViewMainFrame)
        imageViewMainFrame.setImageBitmap(this.drawingViewModel.bitmapMain.value)

        buttonClose = findViewById<Button>(R.id.dialogManageFrameButtonClose)
        buttonClose.setOnClickListener {
            this.cancel()
        }

        buttonAdd = findViewById<Button>(R.id.dialogManageFrameButtonAdd)
        buttonAdd.setOnClickListener {
            val spriteWidth = bitmapManager.bitmapList[0].width
            val spriteHeight = bitmapManager.bitmapList[0].height
            val bitmap = BitmapManager().createBitmap(spriteWidth, spriteHeight)
            bitmapManager.bitmapList.add(bitmap)
            displayFrames()
            activity.displayFrames()
        }

        buttonDuplicate = findViewById(R.id.dialogManageFrameButtonDuplicate)
        buttonDuplicate.setOnClickListener {
            val bitmapCurrent = bitmapManager.bitmapList[bitmapManager.indexCurrent]
            val bitmapDuplicate = bitmapCurrent.copy(bitmapCurrent.config, true)
            bitmapManager.bitmapList.add(bitmapDuplicate)
            displayFrames()
            activity.displayFrames()
        }

        buttonDelete = findViewById(R.id.dialogManageFrameButtonDelete)
        buttonDelete.setOnClickListener {
            if(bitmapManager.bitmapList.size > 1) {
//                Dispatchers.Main.run {
//                    bitmapManager.bitmapList.removeAt(bitmapManager.indexCurrent)
//                    bitmapManager.indexCurrent = 0
//                    activity.setStorageBitmap(0)
//                    displayFrames()
//                    activity.displayFrames()
//                    updateBitmap()
//                }

                bitmapManager.bitmapList.removeAt(bitmapManager.indexCurrent)
                bitmapManager.indexCurrent = 0
                activity.setStorageBitmap(0)
                displayFrames()
                activity.displayFrames()
                updateBitmap()

            }
        }

        displayFrames()
    }

    private fun displayFrames() {
        val linearLayout: LinearLayout = findViewById(R.id.dialogFrameLinearLayout)
        linearLayout.removeAllViews()

        for(i in 0 until bitmapManager.bitmapList.size) {
            val imageButton = createFrameImageButton(i, bitmapManager.bitmapList[i], IMAGE_BUTTON_DIALOG_FRAME_BASE)
            linearLayout.addView(imageButton)
        }
    }

    fun createFrameImageButton(index: Int, bitmap: Bitmap?, idBase: Int) : ImageButton {
        val resources = activity.resources
        val longest = max(resources.displayMetrics.widthPixels, resources.displayMetrics.heightPixels)
        val side = 30 * longest / (250)
        val margin = longest / (250)

        val imageButton = ImageButton(activity.baseContext).apply {
            layoutParams = activity.frameImageParameters(side, side, margin)
            setImageBitmap(bitmap)
            setBackgroundResource(R.mipmap.background16)
            scaleType = ImageView.ScaleType.FIT_CENTER
            adjustViewBounds = true
            setPadding(0, 0, 0,0)
            id = idBase + index

            setOnClickListener {
                activity.setStorageBitmap(index)
                updateBitmap()
            }
        }

        return imageButton
    }
}